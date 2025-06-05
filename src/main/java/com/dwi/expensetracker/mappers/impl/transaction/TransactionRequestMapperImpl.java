package com.dwi.expensetracker.mappers.impl.transaction;

import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionRequestMapperImpl implements Mapper<Transaction, TransactionRequestDto> {

    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public Transaction toEntity(TransactionRequestDto dto) {
        // set user and category from ID
        User user = userService.getById(dto.getUserId());
        Category category = categoryService.getById(dto.getCategoryId());

        return Transaction.builder()
                .user(user)
                .category(category)
                .amount(dto.getAmount())
                .type(dto.getType())
                .description(dto.getDescription())
                .date(dto.getDate())
                .build();
    }

    @Override
    public TransactionRequestDto toDto(Transaction entity) {
        return TransactionRequestDto.builder()
                .userId(entity.getUser().getId())
                .categoryId(entity.getCategory().getId())
                .amount(entity.getAmount())
                .type(entity.getType())
                .description(entity.getDescription())
                .date(entity.getDate())
                .build();
    }
}
