package com.example.saga;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;


import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/workflow")
public class OrchestorService {
	
	@Autowired
	JdbcTemplate  jdbcTemplate;
	
	private final ZeebeClient client;
	
	public OrchestorService(ZeebeClient client) {
		this.client=client;
	}
	
	@PostMapping("/startWorkflow")
	public ResponseEntity<Map<String,Object>> startOrderpaymentWorkFLow(@RequestBody Map<String,Object> orderDetailsDTO) {
		String sagaId="";
		try
		{
		ProcessInstanceEvent resp =	client
		.newCreateInstanceCommand()
		.bpmnProcessId("OrderPayment")
		.latestVersion()
		.variables(orderDetailsDTO)
		.send()
		.join();
		 sagaId=String.valueOf(resp.getProcessInstanceKey());
		System.out.println("Saga Id crated "+sagaId);
	}
	catch(Exception ex){
		System.out.println(ex.getMessage());
	}
		
		
		return ResponseEntity.ok(
		Map.of(
		"sagaId",sagaId,
		"status","STARTED"
		)
		
		);
	}

}
