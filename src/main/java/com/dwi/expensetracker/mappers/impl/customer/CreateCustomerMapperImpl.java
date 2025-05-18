package com.dwi.expensetracker.mappers.impl.customer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.customer.CreateCustomerDto;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CreateCustomerMapperImpl implements Mapper<CustomerEntity, CreateCustomerDto> {
    private final ModelMapper modelMapper;

    public CreateCustomerMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CustomerEntity mapFrom(CreateCustomerDto createCustomerDto) {
        return modelMapper.map(createCustomerDto, CustomerEntity.class);
    }

    @Override
    public CreateCustomerDto mapTo(CustomerEntity customerEntity) {
        return modelMapper.map(customerEntity, CreateCustomerDto.class);
    }

}
