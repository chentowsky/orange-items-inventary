package es.orange.inventory.config;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.state.RocksDBConfigSetter;
import org.rocksdb.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import es.orange.inventory.schema.Schemas;

@Configuration
public class InventoryCalculatorConfig {

	@Value("${schema.registry.url}")
	private String schemaRegistryUrl;

	@Value("${spring.kafka.bootstrap.servers}")
	private String bootstrapServers;

	@Value("${server.host}")
	private String serverHost;

	@Value("${server.port:9094}")
	private int serverPort;

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@PostConstruct
	public void postConstructOperations() {
		Schemas.configureSerdesWithSchemaRegistryUrl(schemaRegistryUrl);
	}

	@Bean("streamsConfig")
	public Properties streamsConfig() throws UnknownHostException {

		Properties props = new Properties();

		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "inventoryCalculator");
		props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, serverHost + ":" + serverPort);
		props.put(StreamsConfig.ROCKSDB_CONFIG_SETTER_CLASS_CONFIG, CustomRocksDBConfig.class);
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 100);

		return props;
	}

	public static class CustomRocksDBConfig implements RocksDBConfigSetter {

		@Override
		public void setConfig(final String storeName, final Options options, final Map<String, Object> configs) {
			// Workaround: We must ensure that the parallelism is set to >= 2. There
			// seems to be a known
			// issue with RocksDB where explicitly setting the parallelism to 1 causes
			// issues (even though
			// 1 seems to be RocksDB's default for this configuration).
			int compactionParallelism = Math.max(Runtime.getRuntime().availableProcessors(), 2);
			// Set number of compaction threads (but not flush threads).
			options.setIncreaseParallelism(compactionParallelism);
		}
	}
}
