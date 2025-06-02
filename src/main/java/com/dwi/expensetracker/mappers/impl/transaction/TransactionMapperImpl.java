package com.dwi.expensetracker.mappers.impl.transaction;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class TransactionMapperImpl implements Mapper<Transaction, TransactionBaseDto> {
    private final ModelMapper modelMapper;

    public TransactionMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Transaction mapFrom(TransactionBaseDto transactionDto) {
        return modelMapper.map(transactionDto, Transaction.class);
    }

    @Override
    public TransactionBaseDto mapTo(Transaction transactionEntity) {
        return modelMapper.map(transactionEntity, TransactionBaseDto.class);
    }

}
