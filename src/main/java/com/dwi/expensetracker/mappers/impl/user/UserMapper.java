package com.dwi.expensetracker.mappers.impl.user;

import org.mapstruct.ReportingPolicy;

import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper extends Mapper<User, UserBaseDto> {

    @Override
    UserBaseDto toDto(User entity);

    @Override
    User toEntity(UserBaseDto dto);

}
