package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CategoryMapperImpl implements Mapper<Category, CategoryBaseDto> {
    private final ModelMapper modelMapper;

    public CategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Category mapFrom(CategoryBaseDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }

    @Override
    public CategoryBaseDto mapTo(Category categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryBaseDto.class);
    }

}
