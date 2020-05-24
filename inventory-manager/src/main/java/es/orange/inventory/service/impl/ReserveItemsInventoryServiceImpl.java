package es.orange.inventory.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.orange.inventory.model.RequestedItem;
import es.orange.inventory.schema.Schemas;
import es.orange.inventory.service.ReserveItemsInventoryService;

@Service
public class ReserveItemsInventoryServiceImpl implements ReserveItemsInventoryService {

	private static Logger LOGGER = LoggerFactory.getLogger(ReserveItemsInventoryServiceImpl.class);

	@Autowired
	private KafkaProducer<RequestedItem, Integer> ItemProducer;

	@Override
	public Integer reserveItemsByType(final String type, final String clietId, final Integer quantity) {

		final RequestedItem reservedItemsRequest = RequestedItem.newBuilder().setType(type).setClientId(clietId).build();
		Future<RecordMetadata> recordfuture = ItemProducer.send(
				new ProducerRecord<>(Schemas.Topics.REQUESTED_ITEMS_BY_CLIENT.name(), reservedItemsRequest, quantity));

		LOGGER.info("Sent inventory reservation {} to topic {}", reservedItemsRequest,
				Schemas.Topics.REQUESTED_ITEMS_BY_CLIENT.name());

		try {
			RecordMetadata metadata = recordfuture.get();
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Error on get metadata : {}", e.getMessage());
			return -1;
		}

		return quantity;

	}
}
