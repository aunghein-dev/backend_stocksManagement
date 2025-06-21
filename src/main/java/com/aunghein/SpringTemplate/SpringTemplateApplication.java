package com.aunghein.SpringTemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aunghein.SpringTemplate.repository")
@EntityScan(basePackages = "com.aunghein.SpringTemplate.model")
public class SpringTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringTemplateApplication.class, args);
	}

}
