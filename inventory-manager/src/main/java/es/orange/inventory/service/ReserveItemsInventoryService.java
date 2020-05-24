package es.orange.inventory.service;

public interface ReserveItemsInventoryService {

	Integer reserveItemsByType(String type, String clietId, Integer quantity);

}
