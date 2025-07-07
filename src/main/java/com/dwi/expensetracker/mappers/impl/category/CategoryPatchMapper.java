package com.dwi.expensetracker.mappers.impl.category;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.dwi.expensetracker.domains.dtos.category.CategoryPatchDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;

@org.mapstruct.Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryPatchMapper extends Mapper<Category, CategoryPatchDto> {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCategoryFromDto(CategoryPatchDto dto, @MappingTarget Category entity);

    @Override
    CategoryPatchDto toDto(Category entity);

    @Override
    Category toEntity(CategoryPatchDto dto);

}
