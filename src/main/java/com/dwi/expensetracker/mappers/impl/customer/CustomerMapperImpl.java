package com.dwi.expensetracker.mappers.impl.customer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.customer.CustomerDto;
import com.dwi.expensetracker.domains.entities.Customer;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CustomerMapperImpl implements Mapper<Customer, CustomerDto> {

    private final ModelMapper modelMapper;

    public CustomerMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Customer mapFrom(CustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }

    @Override
    public CustomerDto mapTo(Customer customerEntity) {
        return modelMapper.map(customerEntity, CustomerDto.class);
    }

}
