package com.example.saga.delegate;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.saga.payment_dto.PaymentRequestDTO;
import com.example.saga.payment_dto.PaymentResponseDTO;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class PaymentWorker {

	private String paymentId;
	String orderId;
	Integer amount;

	private final ZeebeClient client;
	private final RestTemplate restTemplate;

	public PaymentWorker(ZeebeClient client, RestTemplate restTemplate) {
		this.client = client;
		this.restTemplate = restTemplate;
	}

	@PostConstruct
	public void registerWorker() {
		client.newWorker().jobType("payment_worker") // MUST match BPMN
				.handler((client, job) -> {
					PaymentResponseDTO response = null;
					boolean orderStatsus = (boolean) job.getVariablesAsMap().get("orderStatus");
					orderId = (String) job.getVariablesAsMap().get("orderId");
					amount=(Integer) job.getVariablesAsMap().get("debitAmount");
					PaymentRequestDTO requestDTO=new PaymentRequestDTO();
					requestDTO.setOrderId(orderId);
					requestDTO.setAmount(new BigDecimal(amount));
					System.out.println("payment_worker job received" + orderStatsus);
					try {
					
						response = restTemplate.postForObject("http://localhost:9191/payment-service/payment/pay",
								requestDTO, PaymentResponseDTO.class);
						System.out.println("Payment response receieved" + response);
						paymentId = response.getPaymentId();
						boolean paymentStatus = false;
						if (response.getPayStatus().equalsIgnoreCase("DEBITED")) {
							paymentStatus = true;
							client.newCompleteCommand(job.getKey())
									.variables(Map.of("payment", paymentStatus, "orderId", orderId, "paymentId", paymentId)).send()
									.join();
						} else {
							client.newThrowErrorCommand(job.getKey()).errorCode("PAYMENT_FAILED")
									.variables(Map.of("paymentId", response.getPaymentId())).send().join();
						}
					} catch (Exception ex) {
						client.newThrowErrorCommand(job.getKey()).errorCode("PAYMENT_FAILED")
								.variables(Map.of("paymentId", paymentId == null ? "null" : paymentId, "orderId",
										orderId, "payment_status", "FAILED"))
								.errorMessage(ex.getMessage()).send().join();

					}
				}).open();

		System.out.println("Worker registered for payment_worker");
	}

}
