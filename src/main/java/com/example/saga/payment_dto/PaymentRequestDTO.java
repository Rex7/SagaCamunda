package com.example.saga.payment_dto;

import java.math.BigDecimal;

public class PaymentRequestDTO {
	private String orderId;
	private BigDecimal amount;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
