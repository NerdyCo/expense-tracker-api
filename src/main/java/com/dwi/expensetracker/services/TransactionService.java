package com.dwi.expensetracker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.TransactionEntity;

public interface TransactionService {
    TransactionEntity save(TransactionEntity transactionEntity);

    Page<TransactionEntity> findAll(Pageable pageable);

    Optional<TransactionEntity> findOne(Long id);

    boolean doesExist(Long id);

    TransactionEntity partialUpdate(Long id, TransactionEntity transactionEntity);

    void delete(Long id);
}
