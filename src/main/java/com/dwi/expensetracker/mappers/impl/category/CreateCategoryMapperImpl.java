package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CreateCategoryMapperImpl implements Mapper<Category, CategoryRequestDto> {
    private final ModelMapper modelMapper;

    public CreateCategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Category mapFrom(CategoryRequestDto createCategoryDto) {
        return modelMapper.map(createCategoryDto, Category.class);
    }

    @Override
    public CategoryRequestDto mapTo(Category categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryRequestDto.class);
    }

}
