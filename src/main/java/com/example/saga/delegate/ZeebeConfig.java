package com.example.saga.delegate;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.camunda.zeebe.client.ZeebeClient;

@Configuration
public class ZeebeConfig {

  @Bean
  public ZeebeClient zeebeClient() {
    return ZeebeClient.newClientBuilder()
        .gatewayAddress("localhost:26500")
        .usePlaintext()   // 🔥 NO TLS
        .build();
  }
}
