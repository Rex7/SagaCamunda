package com.example.saga.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.saga.controller.OrderRequestDTO;
import com.example.saga.controller.SagaOrderPaymentResponse;

@Repository
public class SageRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public Long insertSaga(OrderRequestDTO orderRequestDTO) {
		return 0L;
	}

	public SagaOrderPaymentResponse getSagaStatus(OrderRequestDTO orderDetails) {
		
		System.out.println("saga id"+orderDetails.getSagaId());
		String sql="""
				select 
				STEP_LOG_ID as stepLogId,
				saga_Id as sagaId,
				step_name as taskName,
				order_id as orderId,
				status as status
				from SAGA_STEP_LOG
				where saga_id = ? and order_id !='0' order by STEP_LOG_ID desc
				
				""";
		List<SagaOrderPaymentResponse> sagaOrderPaymentResponseList=jdbcTemplate.query(sql,
				new Object[] {orderDetails.getSagaId()}, new BeanPropertyRowMapper(SagaOrderPaymentResponse.class));
		return sagaOrderPaymentResponseList.get(0);
	}

}
