package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryMapperImpl implements Mapper<Category, CategoryBaseDto> {
    private final ModelMapper modelMapper;

    @Override
    public Category mapFrom(CategoryBaseDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }

    @Override
    public CategoryBaseDto mapTo(Category categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryBaseDto.class);
    }

}
