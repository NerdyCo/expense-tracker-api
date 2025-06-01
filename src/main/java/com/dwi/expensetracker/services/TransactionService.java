package com.dwi.expensetracker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Transaction;

public interface TransactionService {
    Transaction save(Transaction transactionEntity);

    Page<Transaction> findAll(Pageable pageable);

    Optional<Transaction> findOne(Long id);

    boolean doesExist(Long id);

    Transaction partialUpdate(Long id, Transaction transactionEntity);

    void delete(Long id);
}
