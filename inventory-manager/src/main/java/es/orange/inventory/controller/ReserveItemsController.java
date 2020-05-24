package es.orange.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.orange.inventory.payload.ReserveItemsInventoryPayload;
import es.orange.inventory.service.ReserveItemsInventoryService;

@RequestMapping("/api/inventory")
@RestController
public class ReserveItemsController {

	private static Logger LOGGER = LoggerFactory.getLogger(ReserveItemsController.class);
	
	@Autowired
	private ReserveItemsInventoryService reserveItemsInventoryService;

	@RequestMapping(value = "/items/reserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String reserveItems(@RequestBody ReserveItemsInventoryPayload reserveItemsInventoryPayload) {

		LOGGER.info("Reserve items to inventory: {}", reserveItemsInventoryPayload);

		int reservedItems = reserveItemsInventoryService.reserveItemsByType(reserveItemsInventoryPayload.getType(), reserveItemsInventoryPayload.getClientId(), reserveItemsInventoryPayload.getQuantity());

		return String.format("Requested Items [%d], Reserved Items[%d]", reserveItemsInventoryPayload.getQuantity(), reservedItems
				);
	}

}
