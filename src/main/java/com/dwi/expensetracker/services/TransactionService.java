package com.dwi.expensetracker.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Transaction;

public interface TransactionService {
    Transaction create(Transaction transaction);

    Page<Transaction> getAll(Pageable pageable);

    Transaction getById(UUID id);

    List<Transaction> getByUserId(String userId);

    Transaction updatePartial(UUID id, Transaction transaction);

    void deleteById(UUID id);
}
