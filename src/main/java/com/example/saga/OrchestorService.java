package com.example.saga;

import org.springframework.web.bind.annotation.PostMapping;
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
	public String startOrderpaymentWorkFLow() {
		client
		.newCreateInstanceCommand()
		.bpmnProcessId("OrderPayment")
		.latestVersion()
		.send();
		return "Workflow started";
	}

}
