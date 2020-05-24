package es.orange.inventory.service;

import java.util.List;

import es.orange.inventory.model.Item;

public interface AddItemsInventoryService {

	Long addItemsToStock(List<Item> items);

}
