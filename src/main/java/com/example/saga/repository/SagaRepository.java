package com.example.saga.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.saga.model.SagaStepLog;

@Repository
public interface SagaRepository extends  JpaRepository <SagaStepLog,Long> {

}
