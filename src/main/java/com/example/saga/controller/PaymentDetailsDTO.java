package com.example.saga.controller;

import lombok.Data;

@Data
public class PaymentDetailsDTO {
	private String sagaId;
	private String amount;
	private String orderId;
	private String accountNumber;

}
