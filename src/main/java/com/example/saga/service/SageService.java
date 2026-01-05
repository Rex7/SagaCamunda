package com.example.saga.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.saga.controller.OrderRequestDTO;
import com.example.saga.controller.PaymentDetailsDTO;
import com.example.saga.controller.SagaOrderPaymentResponse;
import com.example.saga.repository.SageRepository;

import io.camunda.zeebe.client.ZeebeClient;

@Service
public class SageService {

	@Autowired
	SageRepository sagaRepo;
	
	
	private final WebClient webClient;
	@Autowired
	ZeebeClient client;
	
	public SageService(WebClient.Builder builder) {
		this.webClient=builder.baseUrl("http://localhost:8080/")
				.build();
	}

	public SagaOrderPaymentResponse getPaymentTask(@RequestBody OrderRequestDTO orderDetails) {
		SagaOrderPaymentResponse orderPaymentResponse = sagaRepo.getSagaStatus(orderDetails);
		return orderPaymentResponse;

	}

	public void completetask(PaymentDetailsDTO paymnetDetailsDTO) {

//		client.newCompleteCommand(Long.valueOf(paymnetDetailsDTO.getSagaId()))
//				.variables(Map.of("paymnetDetails", paymnetDetailsDTO)).send().join();
		
		webClient.patch().
		uri("/v1/tasks/{taskKey}/complete",Long.valueOf(paymnetDetailsDTO.getSagaId()))
		.bodyValue(Map.of("variables",paymnetDetailsDTO))
		.retrieve()
		.bodyToMono(Void.class)
		.block();
		
		
		System.out.println("Completed task " + paymnetDetailsDTO.getSagaId());
	}
}
