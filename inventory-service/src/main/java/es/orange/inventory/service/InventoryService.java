package es.orange.inventory.service;

import java.util.Optional;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import es.orange.inventory.exception.NotFoundException;

public interface InventoryService {
  
  Optional<Integer> getAvailableStockByType(String type) throws NotFoundException;
  
}
