package com.dwi.expensetracker.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionPatchDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final Mapper<Transaction, TransactionBaseDto> transactionBaseMapper;
    private final Mapper<Transaction, TransactionRequestDto> transactionRequestMapper;
    private final Mapper<Transaction, TransactionPatchDto> transactionPatchMapper;

    @PostMapping
    public ResponseEntity<TransactionBaseDto> createTransaction(@Valid @RequestBody TransactionRequestDto requestDto) {
        Transaction transactionToCreate = transactionRequestMapper.toEntity(requestDto);
        Transaction createdTransaction = transactionService.create(transactionToCreate);
        TransactionBaseDto responseDto = transactionBaseMapper.toDto(createdTransaction);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionBaseDto>> getAllTransactions(Pageable pageable) {
        Page<TransactionBaseDto> transactions = transactionService.getAll(pageable).map(transactionBaseMapper::toDto);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionBaseDto> getTransactionById(@PathVariable UUID id) {
        Transaction transaction = transactionService.getById(id);

        return ResponseEntity.ok(transactionBaseMapper.toDto(transaction));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TransactionBaseDto> updateTransactionPartially(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionPatchDto patchDto) {
        Transaction patchRequest = transactionPatchMapper.toEntity(patchDto);
        Transaction updateTransaction = transactionService.updatePartial(id, patchRequest);

        return ResponseEntity.ok(transactionBaseMapper.toDto(updateTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        transactionService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
