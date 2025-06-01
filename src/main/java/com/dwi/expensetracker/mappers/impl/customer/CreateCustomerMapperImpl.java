package com.dwi.expensetracker.mappers.impl.customer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.customer.CreateCustomerDto;
import com.dwi.expensetracker.domains.entities.Customer;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CreateCustomerMapperImpl implements Mapper<Customer, CreateCustomerDto> {
    private final ModelMapper modelMapper;

    public CreateCustomerMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Customer mapFrom(CreateCustomerDto createCustomerDto) {
        return modelMapper.map(createCustomerDto, Customer.class);
    }

    @Override
    public CreateCustomerDto mapTo(Customer customerEntity) {
        return modelMapper.map(customerEntity, CreateCustomerDto.class);
    }

}
