package es.orange.inventory.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import es.orange.inventory.exception.NotFoundException;
import es.orange.inventory.payload.TypeInventoryPayload;
import es.orange.inventory.service.InventoryService;

@RestController
public class InventoryServiceController {
	private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceController.class);

	@Autowired
	private InventoryService invCalculator;


	@RequestMapping(value = "/api/inventory/types/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public TypeInventoryPayload getAvailableInventoryByType(@PathVariable("type") String type, HttpServletRequest request)
			throws NotFoundException {

		LOGGER.info("Received a request for available type [{}] inventory", type);


		Optional<Integer> availableTypeCount = invCalculator.getAvailableStockByType(type);
		return new TypeInventoryPayload(type, availableTypeCount.orElse(0));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleIOException(NotFoundException ex) {

		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

}
