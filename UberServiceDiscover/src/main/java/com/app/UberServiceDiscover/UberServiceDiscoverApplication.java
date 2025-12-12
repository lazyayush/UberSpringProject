package com.app.UberServiceDiscover;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class UberServiceDiscoverApplication {

	public static void main(String[] args) {
		SpringApplication.run(UberServiceDiscoverApplication.class, args);
	}

}
