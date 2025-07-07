package com.dwi.expensetracker.mappers.impl.transaction;

import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.mappers.impl.category.CategoryBaseMapper;
import com.dwi.expensetracker.mappers.impl.user.UserMapper;

@org.mapstruct.Mapper(uses = {
        UserMapper.class,
        CategoryBaseMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransactionBaseMapper extends Mapper<Transaction, TransactionBaseDto> {

    @Override
    @Mapping(target = "user", source = "user")
    @Mapping(target = "category", source = "category")
    TransactionBaseDto toDto(Transaction entity);

    @Override
    @Mapping(target = "user", source = "user")
    @Mapping(target = "category", source = "category")
    Transaction toEntity(TransactionBaseDto dto);

}
