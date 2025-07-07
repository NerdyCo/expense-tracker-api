package com.dwi.expensetracker.mappers.impl.category;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.UserService;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CategoryRequestMapper implements Mapper<Category, CategoryRequestDto> {

    @Autowired
    protected UserService userService;

    @Override
    @Mapping(target = "userId", source = "user.id")
    public abstract CategoryRequestDto toDto(Category entity);

    @Override
    public abstract Category toEntity(CategoryRequestDto dto);

    @AfterMapping
    protected void setUserFromId(CategoryRequestDto dto, @MappingTarget Category category) {
        if (dto.getUserId() != null) {
            User user = userService.getById(dto.getUserId());
            category.setUser(user);
        }
    }
}
