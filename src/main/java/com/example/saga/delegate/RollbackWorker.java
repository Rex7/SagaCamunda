package com.example.saga.delegate;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class RollbackWorker {
	private final ZeebeClient client;
	private final WebClient webClient;
	private final RestTemplate restTemplate;
	Integer paymentId;
	String orderId;

	public RollbackWorker(ZeebeClient client, WebClient webClient,RestTemplate restTemplate) {
		this.client = client;
		this.webClient = webClient;
		this.restTemplate=restTemplate;
	}

	@PostConstruct
	public void registerWorker() {

		client.newWorker().jobType("rollback") // MUST match BPMN
				.handler((client, job) -> {
					String response;
					orderId=(String) job.getVariablesAsMap().get("orderId");
					String paymentID_=(String) job.getVariablesAsMap().get("paymentId");
					if(!"null".equalsIgnoreCase(paymentID_)) {
					 paymentId = (Integer) job.getVariablesAsMap().get("paymentId");
						String paymentStatus = (String) job.getVariablesAsMap().get("payment_status");
						System.out.println("rollback job received");
				          response=restTemplate.postForObject("http://localhost:9191/payment-service/payment/paymnetRefund", paymentId, String.class);
						
						client.newCompleteCommand(job.getKey()).variables("{\"orderstatus\": true}").send().join();
				
					}
					else {
						client.newCompleteCommand(job.getKey())
			              
			              .variables(Map.of(
			                      "paymentId", paymentId==null?"null":paymentId,
			                      "orderId",orderId,
			                      "payment_status", "FAILED"
			                  ))
			
			              .send()
			              .join();
					}
				}).open();

		System.out.println("Worker registered for rollback");
	}

}
