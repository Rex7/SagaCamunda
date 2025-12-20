package com.example.saga.delegate;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class PaymentSuccessWorker {
	
	private final ZeebeClient client;
	private final WebClient webClient;

	  public PaymentSuccessWorker(ZeebeClient client,WebClient webClient) {
	    this.client = client;
	    this.webClient=webClient;
	  }
	
	  @PostConstruct
	  public void registerWorker() {

	    client.newWorker()
	        .jobType("payment_succes")   // MUST match BPMN
	        .handler((client, job) -> {

	          System.out.println("payment success job received");
	          System.out.println("response receieved"+"payment successful worker called");
	          client.newCompleteCommand(job.getKey())
	              .variables("{\"orderstatus\": true}")
	              .send()
	              .join();
	        })
	        .open();

	    System.out.println("Worker registered for payment_success");
	  }
}
