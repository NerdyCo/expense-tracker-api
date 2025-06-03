package com.dwi.expensetracker.mappers.impl.transaction;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionBaseMapperImpl implements Mapper<Transaction, TransactionBaseDto> {
    private final ModelMapper modelMapper;

    @Override
    public Transaction toEntity(TransactionBaseDto dto) {
        return modelMapper.map(dto, Transaction.class);
    }

    @Override
    public TransactionBaseDto toDto(Transaction entity) {
        return modelMapper.map(entity, TransactionBaseDto.class);
    }

}
