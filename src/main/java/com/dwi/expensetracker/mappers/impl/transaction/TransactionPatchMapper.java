package com.dwi.expensetracker.mappers.impl.transaction;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionPatchDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class TransactionPatchMapper implements Mapper<Transaction, TransactionPatchDto> {

    @Autowired
    protected CategoryService categoryService;

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public abstract void updateTransactionFromDto(TransactionPatchDto dto, @MappingTarget Transaction entity);

    @Override
    @Mapping(target = "categoryId", source = "category.id")
    public abstract TransactionPatchDto toDto(Transaction entity);

    @AfterMapping
    protected void resolveCategoryForPatch(TransactionPatchDto dto, @MappingTarget Transaction transaction) {/* ... */
        if (dto.getCategoryId() != null) {
            Category category = categoryService.getById(dto.getCategoryId());

            if (!category.getUser().getId().equals(transaction.getUser().getId())) {
                throw new IllegalArgumentException("Category does not belong to the transaction's user");
            }

            transaction.setCategory(category);
        }
    }

}
