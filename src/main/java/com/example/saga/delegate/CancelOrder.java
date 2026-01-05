package com.example.saga.delegate;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.saga.service.SagaService;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class CancelOrder {

	private final RestTemplate restTemplate;
	private final SagaService sagaService;
	private final ZeebeClient client;

	public CancelOrder(ZeebeClient client, RestTemplate restTemplate, SagaService sagaService) {
		this.restTemplate = restTemplate;
		this.sagaService = sagaService;
		this.client = client;

	}

	@PostConstruct
	public void registerWorker() {

		client.newWorker().jobType("cancel_order") // MUST match BPMN
				.handler((client, job) -> {
					Map<String, Object> response = null;

					System.out.println("cancel_order job received");
					try {
						String orderId = (String) job.getVariablesAsMap().get("orderId");
						sagaService.log(String.valueOf(job.getProcessInstanceKey()),
								String.valueOf(job.getVariablesAsMap().get("orderId")), "Cancel_Order", "Running",
								null);
						Map<String, Object> requestMap = new HashMap<>();
						requestMap.put("orderUid", orderId);
						response = restTemplate.postForObject("http://localhost:9191/order-service/order/cancelOrder",
								requestMap, Map.class);
						System.out.println("Order Cancelled receieved" + response);
						if (response.get("orderStatus").toString().equalsIgnoreCase("cancelled")) {
							sagaService.log(String.valueOf(job.getProcessInstanceKey()),
									String.valueOf(job.getVariablesAsMap().get("orderId")), "Cancel_Order", "Success",
									null);
						} else {
							sagaService.log(String.valueOf(job.getProcessInstanceKey()),
									String.valueOf(job.getVariablesAsMap().get("orderId")), "Cancel_Order", "FAILED",
									null);
						}
					} catch (Exception ex) {
						System.out.println("exception occured" + ex.getMessage());
						sagaService.log(String.valueOf(job.getProcessInstanceKey()),
								String.valueOf(job.getVariablesAsMap().get("orderId")), "Cancel_Order", "FAILED", null);
					}

					client.newCompleteCommand(job.getKey()).send().join();

				})
					.open();
		System.out.println("Worker registered for cancel_order");


	}
}
