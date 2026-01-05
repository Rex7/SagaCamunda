package com.example.saga.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "SAGA_STEP_LOG")
public class SagaStepLog {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SAGA_STEP_SEQ")
	@SequenceGenerator(name = "SAGA_STEP_SEQ", sequenceName = "SAGA_STEP_SEQ", allocationSize = 1)
	private Long STEP_LOG_ID;
	@Column(name = "SAGA_ID")
	private Long sagaId;
	private String sagaInstanceId;
	@Column(name = "order_id")
	private String orderId;
	private String stepName;
	private String status; // STARTED | SUCCESS | FAILED
	private String errorMessage;

	private LocalDateTime createdAt = LocalDateTime.now();
}
