package es.orange.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import es.orange.inventory.payload.AddItemsInventoryPayload;
import es.orange.inventory.payload.ReserveItemsInventoryPayload;

@RequestMapping("/api/inventory")
@RestController
public class InventoryManagerController {

	  private static Logger LOGGER = LoggerFactory.getLogger(InventoryManagerController.class);
	
    @RequestMapping(value = "/items/add", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String addItems(@RequestBody AddItemsInventoryPayload addItemsInventoryPayload) {
    	
    	LOGGER.info("Add items to inventory: {}", addItemsInventoryPayload);
    	
    	return String.format("Added Items Inventory [%s]", addItemsInventoryPayload);
    }

    @RequestMapping(value = "/items/reserve", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String reserveItems(@RequestBody ReserveItemsInventoryPayload reserveItemsInventoryPayload) {
    	
    	LOGGER.info("Reserve items to inventory: {}", reserveItemsInventoryPayload);
    	
    	return String.format("Reserved Items Inventory [%s]", reserveItemsInventoryPayload);
    }

}
