package com.aunghein.SpringTemplate;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aunghein.SpringTemplate.repository")
@EntityScan(basePackages = "com.aunghein.SpringTemplate.model")
@EnableRetry
@EnableScheduling
public class SpringTemplateApplication {

	public static void main(String[] args) {
		// Load .env file variables into system properties (or environment variables)
		// This should be done before SpringApplication.run()
		try {
			Dotenv dotenv = Dotenv.load();
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
				// Alternatively, if you want them as actual environment variables for other processes:
				// System.getenv().put(entry.getKey(), entry.getValue()); // Note: System.getenv() is immutable, this won't work. Use System.setProperty.
			});
			System.out.println(".env file loaded successfully."); // For debugging
		} catch (io.github.cdimascio.dotenv.DotenvException e) {
			System.err.println("Warning: .env file not found or could not be loaded. Relying on system environment variables or application properties. Error: " + e.getMessage());
		}

		SpringApplication.run(SpringTemplateApplication.class, args);
	}

}
