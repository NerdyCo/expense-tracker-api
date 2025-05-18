package com.dwi.expensetracker.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionDto;
import com.dwi.expensetracker.domains.entities.TransactionEntity;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.TransactionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final Mapper<TransactionEntity, TransactionDto> transactionMapper;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto) {
        TransactionEntity transactionEntity = transactionMapper.mapFrom(transactionDto);
        TransactionEntity savedTransactionEntity = transactionService.save(transactionEntity);
        TransactionDto savedDto = transactionMapper.mapTo(savedTransactionEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {
        return transactionService.findOne(id)
                .map(transactionMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<TransactionDto> getAllTransactions(Pageable pageable) {
        return transactionService.findAll(pageable)
                .map(transactionMapper::mapTo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> fullUpdateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionDto transactionDto) {
        if (!transactionService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        transactionDto.setId(id);
        TransactionEntity updatedTransactionEntity = transactionService.save(transactionMapper.mapFrom(transactionDto));
        return ResponseEntity.ok(transactionMapper.mapTo(updatedTransactionEntity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionDto> partialUpdateTrasaction(
            @PathVariable Long id,
            @RequestBody TransactionDto transactionDto) {
        if (!transactionService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        TransactionEntity updatedTransactionEntity = transactionService.partialUpdate(
                id,
                transactionMapper.mapFrom(transactionDto));
        return ResponseEntity.ok(transactionMapper.mapTo(updatedTransactionEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (!transactionService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
