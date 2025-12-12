package com.app.UberEntityService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UberEntityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberEntityServiceApplication.class, args);
	}

}
