package es.orange.inventory.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.orange.inventory.model.Item;
import es.orange.inventory.schema.Schemas;
import es.orange.inventory.service.AddItemsInventoryService;

@Service
public class AddItemsInventoryServiceImpl implements AddItemsInventoryService {

	private static Logger LOGGER = LoggerFactory.getLogger(AddItemsInventoryServiceImpl.class);

	@Autowired
	private KafkaProducer<Item, Integer> itemProducer;

	@Override
	public Long addItemsToStock(List<Item> items) {

		return items.stream()
				.map(item -> itemProducer
						.send(new ProducerRecord<>(Schemas.Topics.ADDED_ITEMS.name(), item, new Integer(1))))
				.map(future -> {
					Optional result = Optional.empty();
					try {
						result = Optional.ofNullable(future.get());
					} catch (InterruptedException | ExecutionException e) {
						LOGGER.error("Error to get metadata record: {}", e.getMessage());
					}
					return result;
				}).filter(Optional::isPresent).count();

	}
	
	
}
