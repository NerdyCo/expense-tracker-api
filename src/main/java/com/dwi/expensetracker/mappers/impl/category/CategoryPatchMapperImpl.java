package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CategoryPatchDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryPatchMapperImpl implements Mapper<Category, CategoryPatchDto> {
    private final ModelMapper modelMapper;

    @Override
    public Category toEntity(CategoryPatchDto dto) {
        return modelMapper.map(dto, Category.class);
    }

    @Override
    public CategoryPatchDto toDto(Category entity) {
        return modelMapper.map(entity, CategoryPatchDto.class);
    }
}
