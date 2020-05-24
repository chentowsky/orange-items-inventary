package es.orange.inventory.controller;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.orange.inventory.model.Item;
import es.orange.inventory.payload.AddItemsInventoryPayload;
import es.orange.inventory.service.AddItemsInventoryService;

@RequestMapping("/api/inventory")
@RestController
public class AddItemsController {

	private static Logger LOGGER = LoggerFactory.getLogger(AddItemsController.class);

	@Autowired
	private AddItemsInventoryService inventoryService;

	@RequestMapping(value = "/items/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String addItems(@RequestBody AddItemsInventoryPayload addItemsInventoryPayload) {

		LOGGER.info("Add items to inventory: {}", addItemsInventoryPayload);

		Long addedItems = inventoryService.addItemsToStock(
					addItemsInventoryPayload.getItems()
					.stream()
					.map(payloadItem -> Item.newBuilder().setId(payloadItem.getId()).setType(payloadItem.getType()).build())
					.collect(Collectors.toList())
				);

		return String.format("Added Items Inventory [%d]", addedItems);
	}

}
