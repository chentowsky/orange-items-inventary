package es.orange.inventory.service;

import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.orange.inventory.Item;
import es.orange.inventory.ReservedItem;
import es.orange.inventory.exception.NotFoundException;
import es.orange.inventory.schema.Schemas.Topics;

@Component
public class InventoryCalculator implements InventoryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryCalculator.class);
  
  @Autowired()
  private Properties streamsConfig;

  private KafkaStreams availableByTypeStream;

  static String AVAILABLE_INVENTORY_STORE = "available_inventory_store";

  @Override
  public Optional<Integer> getAvailableStockByType(String type) throws NotFoundException {
    return Optional.ofNullable(availableByTypeStream
        .store(StoreQueryParameters.fromNameAndType(AVAILABLE_INVENTORY_STORE, QueryableStoreTypes.<String, Integer>keyValueStore())).get(type));
  }

  @PostConstruct
  void start() throws Exception {
    StreamsBuilder kafkaStreamBuilder = new StreamsBuilder();

    KTable<String, Integer> addedTypesToStock = kafkaStreamBuilder
        .stream(Topics.ADDED_ITEMS.name(),
            Consumed.with(Topics.ADDED_ITEMS.keySerde(), Topics.ADDED_ITEMS.valueSerde()))
        .selectKey(new KeyValueMapper<Item, Integer, String>() { //Change the key to type.
        	public String apply(Item key, Integer value) {
        		return key.getType().toString();
        	};
		})
        .groupByKey(Grouped.<String, Integer>with(Serdes.String(), //Group by type.
            Topics.ADDED_ITEMS.valueSerde()))
        .reduce((value1, value2) -> {
          return (value1 + value2);
        });
    
    KTable<String, Integer> reservedByTypes = kafkaStreamBuilder
            .stream(Topics.REQUESTED_ITEMS_BY_CLIENT.name(),
                Consumed.with(Topics.REQUESTED_ITEMS_BY_CLIENT.keySerde(), Topics.REQUESTED_ITEMS_BY_CLIENT.valueSerde()))
            .selectKey(new KeyValueMapper<ReservedItem, Integer, String>() { //Change the key to type.
            	public String apply(ReservedItem key, Integer value) {
            		return key.getType().toString();
            	};
    		})
            .groupByKey(Grouped.<String, Integer>with(Serdes.String(), //Group by type.
                Topics.ADDED_ITEMS.valueSerde()))
            .reduce((value1, value2) -> {
              return (value1 + value2);
            });

    KTable<String, Integer> availableInventory = addedTypesToStock.leftJoin(reservedByTypes,
            (stock, reservations) -> {
              LOGGER.info("Stock:{} Reservation {}", stock, reservations);
              return (stock - (reservations == null ? 0 : reservations));
            }, Materialized.<String, Integer>as(Stores.persistentKeyValueStore(AVAILABLE_INVENTORY_STORE))
                .withKeySerde(Topics.AVAILABLE_TYPE_INVENTORY.keySerde()).withValueSerde(Topics.AVAILABLE_TYPE_INVENTORY.valueSerde()));
    
    //TODO: Implements when a client release a item
    
    //Result to Stream for event sourcing
    availableInventory.toStream().map((key, value) ->{
    	LOGGER.info("Streaming Key: {} Value: {}", key, value);
    	return new KeyValue<String, Integer>(key, value);
    }).to(Topics.AVAILABLE_TYPE_INVENTORY.name(), Produced.<String, Integer>with(Topics.AVAILABLE_TYPE_INVENTORY.keySerde(), Topics.AVAILABLE_TYPE_INVENTORY.valueSerde()));
    
    availableByTypeStream = new KafkaStreams(kafkaStreamBuilder.build(), streamsConfig);
    availableByTypeStream.cleanUp(); //TODO: Delete - only for local testing
    availableByTypeStream.start();    
    
  }

  @PreDestroy
  void stop() {
    if (availableByTypeStream != null) {
      availableByTypeStream.close();
    }
  }

}
