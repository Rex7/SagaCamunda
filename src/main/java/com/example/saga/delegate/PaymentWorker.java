package com.example.saga.delegate;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class PaymentWorker  {
	

	private final ZeebeClient client;
	private final RestTemplate restTemplate;

	  public PaymentWorker(ZeebeClient client,RestTemplate restTemplate) {
	    this.client = client;
	    this.restTemplate=restTemplate;
	  }
	
	
	  @PostConstruct
	  public void registerWorker() {

	    client.newWorker()
	        .jobType("payment_worker")   // MUST match BPMN
	        .handler((client, job) -> {
	       String response=null;
	         boolean orderStatsus=(boolean) job.getVariablesAsMap().get("orderStatus");
	         String orderId=(String) job.getVariablesAsMap().get("orderId");
	         System.out.println("payment_worker job received"+orderStatsus);
	         try {
	         response=restTemplate.postForObject("http://localhost:9191/payment/pay", orderId, String.class);
	         System.out.println("Payment response receieved"+response);
	         boolean paymentStatus=false;
	         if(response.equalsIgnoreCase("Success")) {
	        	 paymentStatus=true;
	          client.newCompleteCommand(job.getKey())
              .variables(Map.of("payment",paymentStatus,"orderId","11","paymentId",2))
	              .send()
	              .join();
	         }
	         else {
	        	 client.newThrowErrorCommand(job.getKey())
	                .errorCode("PAYMENT_FAILED")
	                .variables(Map.of("paymentId",2))
	                .send()
	                .join();
	         }
	         }
	         catch(Exception ex) {
	        	  // Technical failure → retry
	             throw ex;
	         }
	        })
	        .open();

	    System.out.println("Worker registered for payment_worker");
	  }

}
