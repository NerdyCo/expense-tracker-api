package com.dwi.expensetracker.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.CustomerDto;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CustomerMapperImpl implements Mapper<CustomerEntity, CustomerDto> {

    private final ModelMapper modelMapper;

    public CustomerMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerEntity mapFrom(CustomerDto customerDto) {
        return modelMapper.map(customerDto, CustomerEntity.class);
    }

    @Override
    public CustomerDto mapTo(CustomerEntity customerEntity) {
        return modelMapper.map(customerEntity, CustomerDto.class);
    }

}
