package com.example.saga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.EnableZeebeClient;

@SpringBootApplication
@EnableZeebeClient
public class OrderpaymenorchApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderpaymenorchApplication.class, args);
	}

}
