package com.example.saga.dto.order;

public class OrderResponseDTO {
	private String recordupdated;
	private String orderId;
	private String orderStatus;

	public String getRecordupdated() {
		return recordupdated;
	}

	public void setRecordupdated(String recordupdated) {
		this.recordupdated = recordupdated;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

}
