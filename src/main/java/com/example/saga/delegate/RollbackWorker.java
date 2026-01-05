package com.example.saga.delegate;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.saga.service.SagaService;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class RollbackWorker {
	
	private final WebClient webClient;
	private final RestTemplate restTemplate;
	private final SagaService sagaService;
    private final ZeebeClient client;

	Integer paymentId;
	String orderId;

	public RollbackWorker( 
			ZeebeClient client,
			WebClient webClient,
			RestTemplate restTemplate,SagaService sagaService) {
		this.client=client;
		this.webClient = webClient;
		this.restTemplate=restTemplate;
		this.sagaService=sagaService;
	}



	@PostConstruct
	  public void registerWorker() {

	    client.newWorker()
	        .jobType("rollback")   // MUST match BPMN
	        .handler((client, job) -> {
					String response;
					orderId=(String) job.getVariablesAsMap().get("orderId");
					String paymentID=(String) job.getVariablesAsMap().get("paymentId");
					String paymentStatus = (String) job.getVariablesAsMap().get("payment");
	                System.out.println("Payment Status "+paymentStatus);

					sagaService.log(String.valueOf(job.getProcessInstanceKey()), String.valueOf(job.getVariablesAsMap().get("orderId")),
		        			"Rollback", "Running", null);
					if("DEBITED".equalsIgnoreCase(paymentStatus)) {
						System.out.println("Rollback If block executed");
					 paymentId = (Integer) job.getVariablesAsMap().get("paymentId");
						System.out.println("payment status"+paymentStatus);
						System.out.println("rollback job received");
				          response=restTemplate.postForObject("http://localhost:9191/payment-service/payment/paymnetRefund", paymentId, String.class);
				          sagaService.log(String.valueOf(job.getProcessInstanceKey()), String.valueOf(job.getVariablesAsMap().get("orderId")),
				        			"Rollback", "Success", null);
						client.newCompleteCommand(job.getKey()).variables("{\"orderstatus\": true}").send();
						//.join();
				
					}
					else {
						System.out.println("Rollback else block executed");
						client.newCompleteCommand(job.getKey())
			              .variables(Map.of(
			                      "paymentId", paymentID==null?"null":paymentID,
			                      "orderId",orderId,
			                      "payment_status", "FAILED"
			                  ))
			
			              .send();
			             // .join();
					}
	        })
	        .open();
		System.out.println("Worker registered for rollback");
	}

}
