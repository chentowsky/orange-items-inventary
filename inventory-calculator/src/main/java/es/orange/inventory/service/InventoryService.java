package es.orange.inventory.service;

import java.util.Optional;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import es.orange.inventory.exception.NotFoundException;

public interface InventoryService {
  
  @AutoProperty
  static class HostPort {
	private final String host;
	private final int port;

	public HostPort(String host, int port) {
	  this.host = host;
	  this.port = port;
	}

	public String getHost() {
	  return host;
	}

	public int getPort() {
	  return port;
	}
	
	@Override
	public String toString() {
	  return String.format("%s:%s", host, port);
	}

	@Override
	public int hashCode() {
	  return Pojomatic.hashCode(this);
	}

	@Override
	public boolean equals(Object other) {
	  return Pojomatic.equals(this, other);
	}
  }
  
  Optional<Integer> getAvailableStockByType(String type) throws NotFoundException;
}
