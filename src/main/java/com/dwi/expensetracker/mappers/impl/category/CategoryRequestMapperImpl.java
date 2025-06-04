package com.dwi.expensetracker.mappers.impl.category;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryRequestMapperImpl implements Mapper<Category, CategoryRequestDto> {
    private final ModelMapper modelMapper;
    private final UserService userService;

    @Override
    public Category toEntity(CategoryRequestDto dto) {
        Category category = modelMapper.map(dto, Category.class);

        category.setId(null);

        // convert userId into User entity
        User user = userService.getById(dto.getUserId());
        category.setUser(user);

        return category;
    }

    @Override
    public CategoryRequestDto toDto(Category entity) {
        return modelMapper.map(entity, CategoryRequestDto.class);
    }

}
