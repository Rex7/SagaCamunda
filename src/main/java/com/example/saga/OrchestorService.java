package com.example.saga;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.camunda.zeebe.client.ZeebeClient;

@RestController
@RequestMapping("/workflow")
public class OrchestorService {
	private final ZeebeClient client;
	
	public OrchestorService(ZeebeClient client) {
		this.client=client;
	}
	
	@PostMapping("/startWorkflow")
	public String startOrderpaymentWorkFLow(@RequestBody Map<String,Object> orderDetailsDTO) {
		client
		.newCreateInstanceCommand()
		.bpmnProcessId("OrderPayment")
		.latestVersion()
		.variables(orderDetailsDTO)
		.send();
		return "Workflow started";
	}

}
