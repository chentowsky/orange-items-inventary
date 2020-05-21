package es.orange.inventory.controller;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.orange.inventory.ReservedItem;
import es.orange.inventory.payload.ReserveItemsInventoryPayload;
import es.orange.inventory.schema.Schemas;

@RequestMapping("/api/inventory")
@RestController
public class ReserveItemsController {

	private static Logger LOGGER = LoggerFactory.getLogger(ReserveItemsController.class);

	@Autowired
	private KafkaProducer<ReservedItem, Integer> ItemProducer;

	@RequestMapping(value = "/items/reserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String reserveItems(@RequestBody ReserveItemsInventoryPayload reserveItemsInventoryPayload) {

		LOGGER.info("Reserve items to inventory: {}", reserveItemsInventoryPayload);

		Future<RecordMetadata> recordfuture = ItemProducer
				.send(new ProducerRecord<>(Schemas.Topics.REQUESTED_ITEMS_BY_CLIENT.name(),
						new ReservedItem(reserveItemsInventoryPayload.getType(),
								reserveItemsInventoryPayload.getClientId()),
						reserveItemsInventoryPayload.getQuantity()));

		LOGGER.info("Sent inventory reservation {} to topic {}", reserveItemsInventoryPayload,
				Schemas.Topics.REQUESTED_ITEMS_BY_CLIENT.name());

		Optional<String> message = Optional.empty();
		try {
			RecordMetadata metadata = recordfuture.get();
			message = Optional.of(String.format("Requested Items on partition[%s]", metadata.partition()));
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Error on get metadata : {}", e.getMessage());
		}

		return message.orElse("Error on request items");
	}

}
