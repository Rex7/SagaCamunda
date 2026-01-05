package com.example.saga.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.saga.service.SageService;
import com.example.saga.service.TaskRequestDTO;
import com.example.saga.service.TaskService;

@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/saga")
@RestController
public class SagaController {
	@Autowired
	SageService service;
	
	@Autowired
	TaskService taskService;
	
	@PostMapping("/getSaga")
	public ResponseEntity<SagaOrderPaymentResponse> getPaymentTask(@RequestBody  OrderRequestDTO orderDetails) {
		System.out.println("Saga is called ");
		return ResponseEntity.ok(service.getPaymentTask(orderDetails));
	}
	
	@PostMapping("/complete_Task")
	public ResponseEntity<Void> completeTask(@RequestBody PaymentDetailsDTO paymnetDetailsDTO){
		 service.completetask(paymnetDetailsDTO);
		 return ResponseEntity.ok().build();
	}
	
	@PostMapping("/processTask")
	public ResponseEntity<Void> processTask(@RequestBody String taskId){
		System.out.println(taskId);
		taskService.processTask(taskId);
		return ResponseEntity.ok().build();
	}
	@PostMapping("/getTaskId")
	public ResponseEntity<String> getTaskId(@RequestBody TaskRequestDTO taskRequestDTO){
		System.out.println("gettaskId is called and Saga Id "+taskRequestDTO);
		String taskId=taskService.getTaskId(taskRequestDTO);
		System.out.println("taskid "+taskId);
		return ResponseEntity.ok(String.valueOf(taskId));
	}


}
