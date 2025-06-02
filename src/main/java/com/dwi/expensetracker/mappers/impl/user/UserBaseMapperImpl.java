package com.dwi.expensetracker.mappers.impl.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class UserBaseMapperImpl implements Mapper<User, UserBaseDto> {

    private final ModelMapper modelMapper;

    public UserBaseMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User toEntity(UserBaseDto dto) {
        return modelMapper.map(dto, User.class);
    }

    @Override
    public UserBaseDto toDto(User entity) {
        return modelMapper.map(entity, UserBaseDto.class);
    }
}
