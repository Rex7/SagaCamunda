package com.example.saga.delegate;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.saga.dto.order.OrderDetailsDTO;
import com.example.saga.dto.order.OrderResponseDTO;
import com.example.saga.model.SagaStepLog;
import com.example.saga.service.SagaService;

import io.camunda.zeebe.client.ZeebeClient;
import jakarta.annotation.PostConstruct;




@Component
public class CreateOrderWorker {

    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final SagaService sagaService;
    private final ZeebeClient client;
    SagaStepLog currentTask=null;

    public CreateOrderWorker(
    		ZeebeClient client,
            RestTemplate restTemplate,
            JdbcTemplate jdbcTemplate,
            SagaService sagaService) {

        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.sagaService = sagaService;
        this.client=client;
    }

    @PostConstruct
	  public void registerWorker() {
    	client.newWorker()
	        .jobType("create_order")   // MUST match BPMN
	        .handler((client, job) -> {

        Integer amount = (Integer) job.getVariablesAsMap().get("amount");
        List<Integer> productList =
                (List<Integer>) job.getVariablesAsMap().get("productList");
        String userId = (String) job.getVariablesAsMap().get("userId");

        currentTask= sagaService.log(
                String.valueOf(job.getProcessInstanceKey()),
                "0",
                "create_order",
                "Started",
                null
        );

        OrderDetailsDTO requestDTO = new OrderDetailsDTO();
        requestDTO.setProductList(productList);
        requestDTO.setAmount(new BigDecimal(amount));
        requestDTO.setUserId(userId);

        try {
            OrderResponseDTO response =
                    restTemplate.postForObject(
                            "http://localhost:9191/order-service/order/createOrder",
                            requestDTO,
                            OrderResponseDTO.class
                    );

            jdbcTemplate.update("""
                update orders
                set saga_id = ?
                where order_uuid = ?
            """, job.getProcessInstanceKey(), response.getOrderId());

            boolean orderStatus =
                    "created".equalsIgnoreCase(response.getOrderStatus());
            
            Long currentTaskId=currentTask.getSTEP_LOG_ID();
            jdbcTemplate.update("""
                    update SAGA_STEP_LOG
                    set status = 'Completed'
                    where step_log_id = ?
                """, currentTaskId);
            

            client.newCompleteCommand(job.getKey())
                  .variables(Map.of(
                      "orderStatus", orderStatus,
                      "orderId", response.getOrderId(),
                      "debitAmount", amount
                  ))
                  .send();

            sagaService.log(
                    String.valueOf(job.getProcessInstanceKey()),
                    response.getOrderId(),
                    "User Task",
                    "Waiting for User Interventaion",
                    null
            );

        } catch (Exception ex) {

            sagaService.log(
                    String.valueOf(job.getProcessInstanceKey()),
                    "0",
                    "create_order",
                    "Failed",
                    ex.getMessage()
            );

            client.newThrowErrorCommand(job.getKey())
                  .errorCode("ORDER_FAILED")
                  .errorMessage(ex.getMessage())
                  .send();
              //    .join();
        }
    })
	        .open();

	    System.out.println("Worker registered for create_order");
	  }
}
