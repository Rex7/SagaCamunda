package com.example.saga.controller;

import lombok.Data;

@Data
public class OrderRequestDTO {
	private String orderId;
	private String sagaId;

}
