package com.dwi.expensetracker.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.repositories.TransactionRepository;
import com.dwi.expensetracker.services.TransactionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction create(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Page<Transaction> getAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Transaction getById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID " + id));
    }

    @Override
    @Transactional
    public Transaction updatePartial(UUID id, Transaction transaction) {
        return transactionRepository.findById(id).map(existing -> {
            Optional.ofNullable(transaction.getAmount()).ifPresent(existing::setAmount);
            Optional.ofNullable(transaction.getType()).ifPresent(existing::setType);
            Optional.ofNullable(transaction.getDescription()).ifPresent(existing::setDescription);
            Optional.ofNullable(transaction.getDate()).ifPresent(existing::setDate);
            Optional.ofNullable(transaction.getCategory()).ifPresent(existing::setCategory);

            if (transaction.getUser() != null && !transaction.getUser().getId().equals(existing.getUser().getId())) {
                throw new IllegalArgumentException("User cannot be changed for a transaction");
            }

            return transactionRepository.save(existing);
        }).orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID " + id));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with ID " + id);
        }

        transactionRepository.deleteById(id);
    }

}
