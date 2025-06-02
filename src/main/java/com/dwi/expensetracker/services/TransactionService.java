package com.dwi.expensetracker.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Transaction;

public interface TransactionService {
    Transaction save(Transaction transactionEntity);

    Page<Transaction> findAll(Pageable pageable);

    Optional<Transaction> findOne(UUID id);

    boolean doesExist(UUID id);

    Transaction partialUpdate(UUID id, Transaction transactionEntity);

    void delete(UUID id);
}
