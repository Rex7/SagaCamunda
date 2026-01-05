package com.example.saga.delegate;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.saga.service.SagaService;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class PaymentSuccessWorker {

	private final WebClient webClient;
	private final SagaService sagaService;
	private final ZeebeClient client;

	public PaymentSuccessWorker(ZeebeClient client, WebClient webClient, SagaService sagaService) {
		this.webClient = webClient;
		this.client = client;
		this.sagaService = sagaService;
	}

	@PostConstruct
	public void registerWorker() {

		client.newWorker().jobType("payment_succes") // MUST match BPMN
				.handler((client, job) -> {

					sagaService.log(String.valueOf(job.getProcessInstanceKey()),
							String.valueOf(job.getVariablesAsMap().get("orderId")), "PAYMENT_Success", "RUNNING", null);
					System.out.println("payment success job received");
					System.out.println("response receieved" + "payment successful worker called");
					sagaService.log(String.valueOf(job.getProcessInstanceKey()),
							String.valueOf(job.getVariablesAsMap().get("orderId")), "PAYMENT_Success", "Success", null);
					;
					client.newCompleteCommand(job.getKey()).variables("{\"orderstatus\": true}").send().join();

					System.out.println("Worker registered for payment_success");
				}).open();
		System.out.println("Worker registered for payment_success");


	}
}
