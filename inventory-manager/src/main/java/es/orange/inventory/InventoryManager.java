package es.orange.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoConfiguration
@SpringBootApplication
public class InventoryManager {
  public static void main(String[] args) throws Exception {
	SpringApplication.run(InventoryManager.class, args);
  }
}
