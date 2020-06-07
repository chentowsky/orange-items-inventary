package es.orange.inventory.service;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.orange.inventory.schema.Schemas.Topics;

@Component
public class InventoryUpdateListener {
  private static final Logger LOGGER = LoggerFactory.getLogger(InventoryUpdateListener.class);
  
  @Autowired
  private Properties streamsConfig;

  private KafkaStreams availableByTypeEventStream;


  @PostConstruct
  void start() throws Exception {
    StreamsBuilder kafkaStreamBuilder = new StreamsBuilder();

    KStream<String, Integer> inventoryByTypeStream = kafkaStreamBuilder
        .stream(Topics.AVAILABLE_BY_TYPE_INVENTORY.name(),
            Consumed.with(Topics.AVAILABLE_BY_TYPE_INVENTORY.keySerde(), Topics.AVAILABLE_BY_TYPE_INVENTORY.valueSerde()));
    

    inventoryByTypeStream.foreach((type, quantity) -> {
    	LOGGER.info("Safe registry to database: {} - {}", type, quantity);
    	
    	//TODO: Save to Cassandra.
    }); 
    
    availableByTypeEventStream = new KafkaStreams(kafkaStreamBuilder.build(), streamsConfig);
    availableByTypeEventStream.cleanUp(); //TODO: Delete - only for local testing
    availableByTypeEventStream.start();    
    
  }

  @PreDestroy
  void stop() {
    if (availableByTypeEventStream != null) {
      availableByTypeEventStream.close();
    }
  }

}
