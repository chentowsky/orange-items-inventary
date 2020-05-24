package es.orange.inventory.schema;

import static io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes.IntegerSerde;
import org.apache.kafka.common.serialization.Serdes.StringSerde;

import es.orange.inventory.model.Item;
import es.orange.inventory.model.RequestedItem;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

/**
 * This Class is based of:
 * https://github.com/confluentinc/kafka-streams-examples/blob/4.0.0-post/src/main/java/io/confluent/examples/streams/microservices/domain/Schemas.java
 * 
 */
public class Schemas {
	public static class Topic<K, V> {

		private String name;
		private Serde<K> keySerde;
		private Serde<V> valueSerde;

		Topic(String name, Serde<K> keySerde, Serde<V> valueSerde) {
			this.name = name;
			this.keySerde = keySerde;
			this.valueSerde = valueSerde;
			Topics.ALL.put(name, this);
		}

		public Serde<K> keySerde() {
			return keySerde;
		}

		public Serde<V> valueSerde() {
			return valueSerde;
		}

		public String name() {
			return name;
		}

		public String toString() {
			return name;
		}
	}

	public static class Topics {
		public static Serde<Item> ITEM_SERDE = new SpecificAvroSerde<>();
		public static Serde<RequestedItem> REQUESTED_ITEMS_SERDE = new SpecificAvroSerde<>();

		public static Map<String, Topic<?, ?>> ALL = new HashMap<>();
		public static Topic<Item, Integer> ADDED_ITEMS;
		public static Topic<RequestedItem, Integer> REQUESTED_ITEMS_BY_CLIENT;
		public static Topic<String, Integer> AVAILABLE_BY_TYPE_INVENTORY;

		static {
			createTopics();
		}

		private static void createTopics() {
			ADDED_ITEMS = new Topic<>("added-items", ITEM_SERDE, new IntegerSerde());
			REQUESTED_ITEMS_BY_CLIENT = new Topic<>("requested-items-by-client", REQUESTED_ITEMS_SERDE, new IntegerSerde());
			AVAILABLE_BY_TYPE_INVENTORY = new Topic<>("available-inventory", new StringSerde(), new IntegerSerde());
		}
	}

	public static void configureSerdesWithSchemaRegistryUrl(String url) {
		for (Topic<?, ?> topic : Topics.ALL.values()) {
			configure(topic.keySerde(), url, true);
			configure(topic.valueSerde(), url, false);
		}

		schemaRegistryUrl = url;
	}

	private static void configure(Serde<?> serde, String url, boolean key) {
		if (serde instanceof SpecificAvroSerde) {
			serde.configure(Collections.singletonMap(SCHEMA_REGISTRY_URL_CONFIG, url), key);
		}
	}

	public static String schemaRegistryUrl = "";
}
