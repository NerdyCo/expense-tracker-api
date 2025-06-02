package com.dwi.expensetracker.mappers.impl.customer;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CustomerMapperImpl implements Mapper<User, UserBaseDto> {

    private final ModelMapper modelMapper;

    public CustomerMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User mapFrom(UserBaseDto customerDto) {
        return modelMapper.map(customerDto, User.class);
    }

    @Override
    public UserBaseDto mapTo(User customerEntity) {
        return modelMapper.map(customerEntity, UserBaseDto.class);
    }

}
