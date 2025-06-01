package com.dwi.expensetracker.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.repositories.TransactionRepository;
import com.dwi.expensetracker.services.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public boolean doesExist(Long id) {
        return transactionRepository.existsById(id);
    }

    @Override
    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Optional<Transaction> findOne(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Transaction partialUpdate(Long id, Transaction transactionEntity) {
        transactionEntity.setId(id);

        return transactionRepository.findById(id).map(existingTransaction -> {
            Optional.ofNullable(transactionEntity.getAmount()).ifPresent(existingTransaction::setAmount);
            Optional.ofNullable(transactionEntity.getType()).ifPresent(existingTransaction::setType);
            Optional.ofNullable(transactionEntity.getDescription()).ifPresent(existingTransaction::setDescription);
            Optional.ofNullable(transactionEntity.getDate()).ifPresent(existingTransaction::setDate);
            return transactionRepository.save(existingTransaction);
        }).orElseThrow(() -> new RuntimeException("Transaction does not exist"));
    }

    @Override
    public Transaction save(Transaction transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }

}
