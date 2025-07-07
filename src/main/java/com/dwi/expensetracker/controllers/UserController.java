package com.dwi.expensetracker.controllers;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.impl.category.CategoryBaseMapper;
import com.dwi.expensetracker.mappers.impl.transaction.TransactionBaseMapper;
import com.dwi.expensetracker.mappers.impl.user.UserMapper;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.TransactionService;
import com.dwi.expensetracker.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final UserMapper userMapper;
    private final CategoryBaseMapper categoryBaseMapper;
    private final TransactionBaseMapper transactionBaseMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserBaseDto>> getAllUsers(Pageable pageable) {
        Page<UserBaseDto> users = userService.getAll(pageable).map(userMapper::toDto);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserBaseDto> getUserById(@PathVariable String id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @GetMapping("/{id}/categories")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<CategoryBaseDto>> getUserCategories(@PathVariable String id) {
        List<Category> categories = categoryService.getByUserId(id);
        return ResponseEntity.ok(
                categories.stream()
                        .map(categoryBaseMapper::toDto)
                        .toList());
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<TransactionBaseDto>> getUserTransactions(@PathVariable String id) {
        List<Transaction> transactions = transactionService.getByUserId(id);
        return ResponseEntity.ok(
                transactions.stream()
                        .map(transactionBaseMapper::toDto)
                        .toList());
    }
}