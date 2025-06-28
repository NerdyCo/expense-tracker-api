package com.dwi.expensetracker.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.TransactionRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.TransactionService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Transaction create(Transaction transaction) {
        String userId = transaction.getUser().getId();
        UUID categoryId = transaction.getCategory().getId();

        // validate user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID " + userId);
        }

        // validate category exists and belongs to user
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + categoryId));
        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Category does not belong to the user");
        }

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

            if (transaction.getCategory() != null) {
                UUID categoryId = transaction.getCategory().getId();
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + categoryId));

                if (!category.getUser().getId().equals(existing.getUser().getId())) {
                    throw new IllegalArgumentException("Category does not belong to the user");
                }

                existing.setCategory(category);
            }

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

    @Override
    public List<Transaction> getByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID " + userId);
        }

        return transactionRepository.findByUserId(userId);
    }

}
