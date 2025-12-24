package com.example.saga.dto.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderDetailsDTO {
	public List<Integer> getProductList() {
		return productList;
	}
	public void setProductList(List<Integer> productList) {
		this.productList = productList;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	private List<Integer> productList;
	private BigDecimal amount;
	private String userId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}
