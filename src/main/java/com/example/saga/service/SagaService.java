package com.example.saga.service;

import org.springframework.stereotype.Service;

import com.example.saga.model.SagaStepLog;
import com.example.saga.repository.SagaRepository;

@Service
public class SagaService {

    private final SagaRepository repository;

    public SagaService(SagaRepository repository) {
        this.repository = repository;
    }

    public SagaStepLog log(
            String sagaInstanceId,
            String orderId,
            String stepName,
            String status,
            String errorMessage
    ) {
        SagaStepLog log = new SagaStepLog();
        log.setSagaInstanceId(sagaInstanceId);
        log.setSagaId(Long.valueOf(sagaInstanceId));
        log.setOrderId(orderId);
        log.setStepName(stepName);
        log.setStatus(status);
        log.setErrorMessage(errorMessage);

        SagaStepLog savedObj= repository.save(log);
        System.out.println("Saved object"+savedObj);
        return savedObj;
    }
}
