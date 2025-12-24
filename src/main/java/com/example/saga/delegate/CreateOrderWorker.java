package com.example.saga.delegate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.saga.dto.order.OrderDetailsDTO;
import com.example.saga.dto.order.OrderResponseDTO;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;

@Component
public class CreateOrderWorker  {
	
	
	private final ZeebeClient client;
	private final WebClient webClient;
	private final RestTemplate restTemplate;

	  public CreateOrderWorker(ZeebeClient client,WebClient webClient,RestTemplate restTemplate) {
	    this.client = client;
	    this.webClient=webClient;
	    this.restTemplate=restTemplate;
	  }
	
	  @PostConstruct
	  public void registerWorker() {

	    client.newWorker()
	        .jobType("create_order")   // MUST match BPMN
	        .handler((client, job) -> {
	        	OrderResponseDTO response = null;
	        	OrderDetailsDTO requestDTO=new OrderDetailsDTO();
	        	Integer amount=(Integer) job.getVariablesAsMap().get("amount");
	        	List<Integer> productList= (List<Integer>) job.getVariablesAsMap().get("productList");
	        	String userId=(String) job.getVariablesAsMap().get("userId");
	        	requestDTO.setProductList(productList);
	        	requestDTO.setAmount(new BigDecimal(amount));
	        	requestDTO.setUserId(userId);

	          System.out.println("create_order job received");
	          try {
	          response=restTemplate.postForObject("http://localhost:9191/order-service/order/createOrder", requestDTO, OrderResponseDTO.class);
	          System.out.println("response receieved"+response);
	          }
	          catch(Exception ex) {
	        	  System.out.println("exception occured"+ex.getMessage());
	          }
	          boolean orderstatus =false;
	          String orderStat=response.getOrderStatus();
	          if("created".equalsIgnoreCase(orderStat)) {
	        	  orderstatus=true;
	          }
	          client.newCompleteCommand(job.getKey())
	              .variables(Map.of("orderStatus",orderstatus,"orderId",response.getOrderId(),"debitAmount",new BigDecimal(amount),
	            		  "payment_status","null","paymentId","null"))
	              .send()
	              .join();
	        })
	        .open();

	    System.out.println("Worker registered for create_order");
	  }
	}
	

