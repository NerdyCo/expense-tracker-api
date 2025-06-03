package com.dwi.expensetracker.mappers.impl.transaction;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionPatchDto;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionPatchMapperImpl implements Mapper<Transaction, TransactionPatchDto> {
    private final ModelMapper modelMapper;

    @Override
    public Transaction toEntity(TransactionPatchDto dto) {
        return modelMapper.map(dto, Transaction.class);
    }

    @Override
    public TransactionPatchDto toDto(Transaction entity) {
        return modelMapper.map(entity, TransactionPatchDto.class);
    }

}
