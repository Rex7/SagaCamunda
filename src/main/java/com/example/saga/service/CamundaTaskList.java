package com.example.saga.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CamundaTaskList {

	private final WebClient webClient;
	public CamundaTaskList(WebClient webClient) {
		this.webClient=webClient;
	}
	
	 public Map<String, Object> findPaymentTask(Long orderId) {

	        Map<String, Object> payload = Map.of(
	            "pageSize", 10,
	            "filter", Map.of(
	                "variables", List.of(
	                    Map.of(
	                        "name", "orderId",
	                        "operator", "eq",
	                        "value", orderId.toString()
	                    )
	                )
	            )
	        );

	        return webClient.post()
	            .uri("/v1/tasks/search")
	            .bodyValue(payload)
	            .retrieve()
	            .bodyToMono(Map.class)
	            .block();
	    }
}
