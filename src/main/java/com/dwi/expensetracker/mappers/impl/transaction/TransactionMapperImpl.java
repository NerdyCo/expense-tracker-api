package com.dwi.expensetracker.mappers.impl.transaction;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionDto;
import com.dwi.expensetracker.domains.entities.TransactionEntity;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class TransactionMapperImpl implements Mapper<TransactionEntity, TransactionDto> {
    private final ModelMapper modelMapper;

    public TransactionMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public TransactionEntity mapFrom(TransactionDto transactionDto) {
        return modelMapper.map(transactionDto, TransactionEntity.class);
    }

    @Override
    public TransactionDto mapTo(TransactionEntity transactionEntity) {
        return modelMapper.map(transactionEntity, TransactionDto.class);
    }

}
