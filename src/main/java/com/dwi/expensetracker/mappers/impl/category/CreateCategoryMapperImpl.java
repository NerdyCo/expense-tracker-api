package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CreateCategoryDto;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.mappers.Mapper;

@Component
public class CreateCategoryMapperImpl implements Mapper<CategoryEntity, CreateCategoryDto> {
    private final ModelMapper modelMapper;

    public CreateCategoryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryEntity mapFrom(CreateCategoryDto createCategoryDto) {
        return modelMapper.map(createCategoryDto, CategoryEntity.class);
    }

    @Override
    public CreateCategoryDto mapTo(CategoryEntity categoryEntity) {
        return modelMapper.map(categoryEntity, CreateCategoryDto.class);
    }

}
