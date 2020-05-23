package es.orange.inventory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * Ensure you have started full suite of applications before running this test
 * 
 */
public class InventoryIntegrationTest {

	private static final int MOBILE_AMOUNT_DECREMENT = 3;
	private static final int MOBILE_AMOUNT_INCREMENT = 15;
	private static final int ROUTER_AMOUNT_DECREMENT = 4;
	private static final int ROUTER_AMOUNT_INCREMENT = 12;
	private static final String HOST = "http://192.168.99.100";
	private static final String AVAILABLE_INVENTORY_API_URL = HOST+":9091/api/inventory/types/{type}";
	private static final String INVENTORY_API_URL = HOST+":9090/api/inventory/items";
	private static final String INVENTORY_API_RESERVE_ITEMS = INVENTORY_API_URL+"/reserve";
	private static final String INVENTORY_API_ADD_STOCK = INVENTORY_API_URL+"/add";

	
	private static final String MOBILE_TYPE = "movil";
	private static final String ROUTER_TYPE = "router";

	private RestTemplate restTemplate = new RestTemplate(getMessageConverters());

	@Test
	public void testMobileInventory() throws InterruptedException {

		int initialMobileInventoryAmout = getAvailableInventoryAmount(MOBILE_TYPE);
		
		
		addItemsToInventary(MOBILE_TYPE, MOBILE_AMOUNT_INCREMENT);

		Thread.sleep(1000); //TODO Change to async way

		int expectedMobileInventoryAmout = initialMobileInventoryAmout + MOBILE_AMOUNT_INCREMENT;

		assertThat("Mobile Inventory must have the inventory update", getAvailableInventoryAmount(MOBILE_TYPE),
				is(expectedMobileInventoryAmout));

		// Reserve inventory
		reserveInventory(MOBILE_TYPE, MOBILE_AMOUNT_DECREMENT);

		Thread.sleep(1000); //TODO Change to async way

		expectedMobileInventoryAmout -= MOBILE_AMOUNT_DECREMENT;

		assertThat("Mobile Inventory must have the inventory update", getAvailableInventoryAmount(MOBILE_TYPE),
				is(expectedMobileInventoryAmout));
		
	}
	
	@Test
	public void testRouterInventory() throws InterruptedException {
		int initialRouterInventoryAmout = this.getAvailableInventoryAmount(ROUTER_TYPE);
		
		addItemsToInventary(ROUTER_TYPE, ROUTER_AMOUNT_INCREMENT);

		Thread.sleep(1000); //TODO Change to async way

		int expectedRouterInventoryAmout = initialRouterInventoryAmout + ROUTER_AMOUNT_INCREMENT;

		assertThat("Mobile Inventory must have the inventory update", getAvailableInventoryAmount(ROUTER_TYPE),
				is(expectedRouterInventoryAmout));

		// Reserve router items
		reserveInventory(ROUTER_TYPE, ROUTER_AMOUNT_DECREMENT);

		Thread.sleep(1000); //TODO Change to async way

		expectedRouterInventoryAmout -= ROUTER_AMOUNT_INCREMENT;

		assertThat("Mobile Inventory must have the inventory update", getAvailableInventoryAmount(ROUTER_TYPE),
				is(expectedRouterInventoryAmout));
		
	}

	private void addItemsToInventary(String type, int mount) {
		
		List<Item> itemsToStock = generateRandomItems(type, mount);
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
		headers.setContentType(MediaType.APPLICATION_JSON);

//		String jsonItemStr =new Gson().toJson(new ItemList(itemsToStock));
				
		ResponseEntity<String> exchange = restTemplate.exchange(INVENTORY_API_ADD_STOCK, HttpMethod.POST,
				new HttpEntity<ItemList>(	new ItemList(itemsToStock), headers), String.class);

		assertTrue(exchange.getStatusCode().equals(HttpStatus.OK));
	}

	private List<Item> generateRandomItems(String type, int amount) {
		
		List<Item> items = Lists.newArrayList();
		
		for (int i =1; i <= amount; i++) {
			items.add(new Item(String.format("%09d", new Double(Math.random() * 100000000).longValue()), type));
		}
		return items;
		
	}

	private void reserveInventory(String type, int quantity) {
		InventoryByType amountOfItemTypeToReserve = new InventoryByType(type, quantity);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		ResponseEntity<String> exchange = restTemplate.exchange(INVENTORY_API_RESERVE_ITEMS, HttpMethod.POST,
				new HttpEntity<InventoryByType>(amountOfItemTypeToReserve, headers), String.class);

		assertTrue(exchange.getStatusCode().equals(HttpStatus.OK));
	}
	
	private int getAvailableInventoryAmount(String type) {
		Map<String, String> pathVars = Maps.newHashMap();
		pathVars.put("type", type);
		try {
			InventoryByType inventory = restTemplate.getForObject(
					AVAILABLE_INVENTORY_API_URL, InventoryByType.class, pathVars);

			return inventory.getCount();
		} catch (RestClientException e) {
			return 0;
		}
	}
	
	private List<HttpMessageConverter<?>> getMessageConverters() {
	    List<HttpMessageConverter<?>> converters = 
	      new ArrayList<HttpMessageConverter<?>>();
	    converters.add(new MappingJackson2HttpMessageConverter());
	    return converters;
	}
	
	
	static class ItemList{
		private List<Item> items;

		public ItemList(List<Item> items) {
			this.items = items;
		}

		public List<Item> getItems() {
			return items;
		}

		public void setItems(List<Item> items) {
			this.items = items;
		}
		
	}

	static class Item {
		private String id;
		private String type;
		
		public Item() {
			
		}

		public Item(String id, String type) {
			this.id = id;
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}
	
	static class InventoryByType {
		private String type;
		private int count;
		
		public InventoryByType() {
		}
		
		public InventoryByType(String type, int count) {
			this.type = type;
			this.count = count;
		}
		
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}

		
	}
}
