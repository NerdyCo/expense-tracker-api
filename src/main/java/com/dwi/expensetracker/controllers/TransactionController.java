package com.dwi.expensetracker.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.dwi.expensetracker.mappers.impl.transaction.TransactionBaseMapper;
import com.dwi.expensetracker.mappers.impl.transaction.TransactionPatchMapper;
import com.dwi.expensetracker.mappers.impl.transaction.TransactionRequestMapper;
import com.dwi.expensetracker.services.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionBaseMapper transactionBaseMapper;
    private final TransactionRequestMapper transactionRequestMapper;
    private final TransactionPatchMapper transactionPatchMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionBaseDto> createTransaction(@Valid @RequestBody TransactionRequestDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(requestDto.getUserId())) {
            throw new IllegalStateException("You can only create transactions for yourself");
        }

        Transaction transactionToCreate = transactionRequestMapper.toEntity(requestDto);
        Transaction createdTransaction = transactionService.create(transactionToCreate);
        TransactionBaseDto responseDto = transactionBaseMapper.toDto(createdTransaction);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<TransactionBaseDto>> getAllTransactions(Pageable pageable) {
        Page<TransactionBaseDto> transactions = transactionService.getAll(pageable).map(transactionBaseMapper::toDto);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<TransactionBaseDto> getTransactionById(@PathVariable UUID id) {
        Transaction transaction = transactionService.getById(id);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!transaction.getUser().getId().equals(userId) && !SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("You can only view your own transactions");
        }

        return ResponseEntity.ok(transactionBaseMapper.toDto(transaction));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionBaseDto> updateTransactionPartially(
            @PathVariable UUID id,
            @Valid @RequestBody TransactionPatchDto patchDto) {
        Transaction transaction = transactionService.getById(id);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only update your own transactions");
        }

        transactionPatchMapper.updateTransactionFromDto(patchDto, transaction);

        Transaction updatedTransaction = transactionService.updatePartial(id, transaction);

        return ResponseEntity.ok(transactionBaseMapper.toDto(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID id) {
        Transaction transaction = transactionService.getById(id);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!transaction.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own transactions");
        }

        transactionService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
