package com.dwi.expensetracker.mappers.impl.transaction;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionRequestMapperImpl implements Mapper<Transaction, TransactionRequestDto> {

    private final ModelMapper modelMapper;

    @Override
    public Transaction toEntity(TransactionRequestDto dto) {
        return modelMapper.map(dto, Transaction.class);
    }

    @Override
    public TransactionRequestDto toDto(Transaction entity) {
        return modelMapper.map(entity, TransactionRequestDto.class);
    }
}
