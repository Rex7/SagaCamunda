package com.example.saga.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TaskSearchResponse {
	private List<Map<String,Object>> items;
	private int total;

}
