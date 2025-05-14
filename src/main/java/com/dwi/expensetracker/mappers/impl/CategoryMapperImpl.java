package com.dwi.expensetracker.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.CategoryDto;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CategoryMapperImpl implements Mapper<CategoryEntity, CategoryDto> {
    private final ModelMapper modelMapper;

    public CategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryEntity mapFrom(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, CategoryEntity.class);
    }

    @Override
    public CategoryDto mapTo(CategoryEntity categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryDto.class);
    }

}
