package es.orange.inventory.config;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.orange.inventory.model.Item;
import es.orange.inventory.model.RequestedItem;
import es.orange.inventory.schema.Schemas;

@Configuration
public class KafkaConfiguration {

	  @Value("${spring.kafka.bootstrap.servers}")
	  private String bootstrapServers;
	  
	  private static <K, V> KafkaProducer<K, V> startProducer(String bootstrapServers,
	      Serde<K> keySerde, Serde<V> valueSerde) {
	    Properties producerConfig = new Properties();
	    
	    producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
	    producerConfig.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
	    producerConfig.put(ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE));
	    producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
	   
	    return new KafkaProducer<>(producerConfig,
	        keySerde.serializer(),
	        valueSerde.serializer());
	  }
	  
	  @Bean
	  public KafkaProducer<Item, Integer> itemProducer() {
		return startProducer(bootstrapServers, Schemas.Topics.ITEM_SERDE, 
			new Serdes.IntegerSerde());
	  }

	  @Bean
	  public KafkaProducer<RequestedItem, Integer> reservedItemProducer() {
		return startProducer(bootstrapServers, Schemas.Topics.REQUESTED_ITEMS_SERDE, 
			new Serdes.IntegerSerde());
	  }
	  
	  
	  @Value("${schema.registry.url}")
	  private String schemaRegistryUrl;
	  

	  @PostConstruct
	  public void configureSchemaRegistry() {
		Schemas.configureSerdesWithSchemaRegistryUrl(schemaRegistryUrl);
	  }
	  
	  @PreDestroy
	  public void shutdown() {
		itemProducer().close();
		reservedItemProducer().close();
	  }
	
}
