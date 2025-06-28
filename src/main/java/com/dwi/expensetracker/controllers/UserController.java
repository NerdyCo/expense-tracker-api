package com.dwi.expensetracker.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserPatchDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.TransactionService;
import com.dwi.expensetracker.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final Mapper<User, UserBaseDto> userBaseMapper;
    private final Mapper<Category, CategoryBaseDto> categoryBaseMapper;
    private final Mapper<Transaction, TransactionBaseDto> transactionBaseMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserBaseDto>> getAllUsers(Pageable pageable) {
        Page<UserBaseDto> users = userService.getAll(pageable).map(userBaseMapper::toDto);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserBaseDto> getUserById(@PathVariable String id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userBaseMapper.toDto(user));
    }

    @GetMapping("/{id}/categories")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<CategoryBaseDto>> getUserCategories(@PathVariable String id) {
        List<Category> categories = categoryService.getByUserId(id);
        return ResponseEntity.ok(
                categories.stream()
                        .map(categoryBaseMapper::toDto)
                        .toList());
    }

    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TransactionBaseDto>> getUserTransactions(@PathVariable String id) {
        List<Transaction> transactions = transactionService.getByUserId(id);
        return ResponseEntity.ok(
                transactions.stream()
                        .map(transactionBaseMapper::toDto)
                        .toList());
    }
}