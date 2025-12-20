package com.example.saga.delegate;

import org.springframework.stereotype.Component;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class BpmnDeployer {

  private final ZeebeClient client;

  public BpmnDeployer(ZeebeClient client) {
    this.client = client;
  }

  @PostConstruct
  public void deploy() {
    client.newDeployResourceCommand()
        .addResourceFromClasspath("orderPayment.bpmn")
        .send()
        .join();

    System.out.println("BPMN deployed successfully");
  }
}