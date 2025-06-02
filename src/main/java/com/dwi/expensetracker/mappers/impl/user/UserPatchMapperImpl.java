package com.dwi.expensetracker.mappers.impl.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.user.UserPatchDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class UserPatchMapperImpl implements Mapper<User, UserPatchDto> {
    private final ModelMapper modelMapper;

    public UserPatchMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public User toEntity(UserPatchDto dto) {
        return modelMapper.map(dto, User.class);
    }

    @Override
    public UserPatchDto toDto(User entity) {
        return modelMapper.map(entity, UserPatchDto.class);
    }
}
