package com.vendaingressos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VendaIngressosApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendaIngressosApplication.class, args);
	}

}