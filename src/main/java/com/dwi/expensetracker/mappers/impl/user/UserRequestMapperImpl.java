package com.dwi.expensetracker.mappers.impl.user;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserRequestMapperImpl implements Mapper<User, UserRequestDto> {
    private final ModelMapper modelMapper;

    @Override
    public User toEntity(UserRequestDto dto) {
        return modelMapper.map(dto, User.class);
    }

    @Override
    public UserRequestDto toDto(User entity) {
        return modelMapper.map(entity, UserRequestDto.class);
    }
}
