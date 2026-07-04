package com.app.UberLocationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan("com.app.UberEntityService.models")
public class  UberLocationServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UberLocationServiceApplication.class, args);
	}
}
