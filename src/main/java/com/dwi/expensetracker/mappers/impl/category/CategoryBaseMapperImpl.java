package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryBaseMapperImpl implements Mapper<Category, CategoryBaseDto> {
    private final ModelMapper modelMapper;

    @Override
    public Category toEntity(CategoryBaseDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    @Override
    public CategoryBaseDto toDto(Category entity) {
        return modelMapper.map(entity, CategoryBaseDto.class);
    }

}
