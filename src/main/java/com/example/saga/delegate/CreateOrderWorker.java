package com.example.saga.delegate;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class CreateOrderWorker  {
	
	
	private final ZeebeClient client;
	private final WebClient webClient;
	private final RestTemplate restTemplate;

	  public CreateOrderWorker(ZeebeClient client,WebClient webClient,RestTemplate restTemplate) {
	    this.client = client;
	    this.webClient=webClient;
	    this.restTemplate=restTemplate;
	  }
	
	  @PostConstruct
	  public void registerWorker() {

	    client.newWorker()
	        .jobType("create_order")   // MUST match BPMN
	        .handler((client, job) -> {
	        	Map<String,Object> response=null;
	        	String orderIdUpdated=(String) job.getVariablesAsMap().get("orderId");
	          System.out.println("create_order job received");
	          try {
	          response=restTemplate.postForObject("http://localhost:9191/order/createOrder", "01", Map.class);
	          System.out.println("response receieved"+response);
	          }
	          catch(Exception ex) {
	        	  System.out.println("exception occured"+ex.getMessage());
	          }
	          boolean orderstatus =false;
	          String orderStat=response.get("orderStatus").toString();
	          if("created".equalsIgnoreCase(orderStat)) {
	        	  orderstatus=true;
	          }
	          client.newCompleteCommand(job.getKey())
	              .variables(Map.of("orderStatus",orderstatus,"orderId","11"))
	              .send()
	              .join();
	        })
	        .open();

	    System.out.println("Worker registered for create_order");
	  }
	}
	

