package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CreateCategoryDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CreateCategoryMapperImpl implements Mapper<Category, CreateCategoryDto> {
    private final ModelMapper modelMapper;

    public CreateCategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public Category mapFrom(CreateCategoryDto createCategoryDto) {
        return modelMapper.map(createCategoryDto, Category.class);
    }

    @Override
    public CreateCategoryDto mapTo(Category categoryEntity) {
        return modelMapper.map(categoryEntity, CreateCategoryDto.class);
    }

}
