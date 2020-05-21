package es.orange.inventory.controller;

import java.util.concurrent.ExecutionException;

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

import es.orange.inventory.Item;
import es.orange.inventory.payload.AddItemsInventoryPayload;
import es.orange.inventory.payload.ReserveItemsInventoryPayload;
import es.orange.inventory.schema.Schemas;

@RequestMapping("/api/inventory")
@RestController
public class AddItemsController {

	private static Logger LOGGER = LoggerFactory.getLogger(AddItemsController.class);

	@Autowired
	private KafkaProducer<Item, Integer> producer;

	@RequestMapping(value = "/items/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String addItems(@RequestBody AddItemsInventoryPayload addItemsInventoryPayload) {

		LOGGER.info("Add items to inventory: {}", addItemsInventoryPayload);

		Long addedItems = addItemsInventoryPayload.getItems().stream()
				.map(item -> producer.send(new ProducerRecord<>(Schemas.Topics.ITEMS_INVENTORY.name(),
						new Item(item.getId(), item.getType()), new Integer(1))))
				.map(future -> {
					RecordMetadata metadataResult = null;
					try {
						metadataResult = future.get();
					} catch (InterruptedException | ExecutionException e) {
						LOGGER.error("Error to get metadata record: {}", e.getMessage());
					}
					return metadataResult;
				}).filter(result -> result != null).count();

		return String.format("Added Items Inventory [%d]", addedItems);
	}

}
