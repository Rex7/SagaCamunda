package com.example.saga.delegate;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class CancelOrder {
	private final ZeebeClient client;
	private final RestTemplate restTemplate;
	
	public CancelOrder(ZeebeClient client,RestTemplate restTemplate) {
		this.client=client;
		this.restTemplate=restTemplate;
		
	}
	 @PostConstruct
	  public void registerWorker() {

	    client.newWorker()
	        .jobType("cancel_order")   // MUST match BPMN
	        .handler((client, job) -> {
	        	Map<String,Object> response=null;

	          System.out.println("cancel_order job received");
	          try {
	          Integer orderId=Integer.parseInt( job.getVariablesAsMap().get("orderId").toString());
	          response=restTemplate.postForObject("http://localhost:9191/order/cancelOrder", orderId, Map.class);
	          System.out.println("Order Cancelled receieved"+response);
	          }
	          catch(Exception ex) {
	        	  System.out.println("exception occured"+ex.getMessage());
	          }
	       
	          client.newCompleteCommand(job.getKey())
	              .send()
	              .join();
	        })
	        .open();

	    System.out.println("Worker registered for cancel_order");
	  }

}
