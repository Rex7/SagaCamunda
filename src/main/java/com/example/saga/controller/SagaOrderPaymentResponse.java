package com.example.saga.controller;

import lombok.Data;

@Data
public class SagaOrderPaymentResponse {
	
	private String taskId;
	private String taskName;
	private String status;
	private String sagaId;
	private String stepLogId;
	private String orderId;

}
