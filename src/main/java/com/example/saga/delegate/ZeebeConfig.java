package com.example.saga.delegate;


import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.ZeebeClientBuilder;
@Configuration
public class ZeebeConfig {

	 @Bean
	  public ZeebeClient zeebeClient() {
	    return ZeebeClient.newClientBuilder()
	        .gatewayAddress("localhost:26500")
	        .usePlaintext()   // 🔥 NO TLS
	        .defaultRequestTimeout(Duration.ofSeconds(60))
	        .build();
	  }
}
