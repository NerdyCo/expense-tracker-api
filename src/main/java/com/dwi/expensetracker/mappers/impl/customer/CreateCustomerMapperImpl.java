package com.dwi.expensetracker.mappers.impl.customer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CreateCustomerMapperImpl implements Mapper<User, UserRequestDto> {
    private final ModelMapper modelMapper;

    public CreateCustomerMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User mapFrom(UserRequestDto createCustomerDto) {
        return modelMapper.map(createCustomerDto, User.class);
    }

    @Override
    public UserRequestDto mapTo(User customerEntity) {
        return modelMapper.map(customerEntity, UserRequestDto.class);
    }

}
