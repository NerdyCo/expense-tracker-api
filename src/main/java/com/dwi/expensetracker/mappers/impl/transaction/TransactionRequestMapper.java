package com.dwi.expensetracker.mappers.impl.transaction;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.UserService;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TransactionRequestMapper implements Mapper<Transaction, TransactionRequestDto> {

    @Autowired
    protected UserService userService;

    @Autowired
    protected CategoryService categoryService;

    @Override
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "categoryId", source = "category.id")
    public abstract TransactionRequestDto toDto(Transaction entity);

    @Override
    public abstract Transaction toEntity(TransactionRequestDto dto);

    @AfterMapping
    protected void resolveAssociations(TransactionRequestDto dto, @MappingTarget Transaction transaction) {
        if (dto.getUserId() != null) {
            User user = userService.getById(dto.getUserId());
            transaction.setUser(user);
        }

        if (dto.getCategoryId() != null) {
            Category category = categoryService.getById(dto.getCategoryId());
            transaction.setCategory(category);

            if (transaction.getUser() != null && !category.getUser().getId().equals(transaction.getUser().getId())) {
                throw new IllegalArgumentException("Category does not belong to the specified user.");
            }
        }
    }
}
