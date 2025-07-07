package com.dwi.expensetracker.mappers.impl.category;

import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.mappers.impl.user.UserMapper;

@org.mapstruct.Mapper(uses = { UserMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryBaseMapper extends Mapper<Category, CategoryBaseDto> {

    @Override
    @Mapping(target = "user", source = "user")
    CategoryBaseDto toDto(Category entity);

    @Override
    @Mapping(target = "user", source = "user")
    Category toEntity(CategoryBaseDto dto);

}
