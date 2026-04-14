package com.skaly.fashion_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FashionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FashionBackendApplication.class, args);
	}

}
