package com.dwi.expensetracker.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.category.CategoryDto;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final Mapper<CategoryEntity, CategoryDto> categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        CategoryEntity categoryEntity = categoryMapper.mapFrom(categoryDto);
        CategoryEntity savedCategoryEntity = categoryService.save(categoryEntity);
        CategoryDto savedDto = categoryMapper.mapTo(savedCategoryEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long id) {
        return categoryService.findOne(id)
                .map(categoryMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<CategoryDto> getAllCategories(Pageable pageable) {
        return categoryService.findAll(pageable)
                .map(categoryMapper::mapTo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> fullUpdateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto categoryDto) {
        if (!categoryService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        categoryDto.setId(id);
        CategoryEntity updatedCategoryEntity = categoryService.save(categoryMapper.mapFrom(categoryDto));
        return ResponseEntity.ok(categoryMapper.mapTo(updatedCategoryEntity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto> partialUpdateCategory(
            @PathVariable Long id,
            @RequestBody CategoryDto categoryDto) {
        if (!categoryService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        CategoryEntity updatedCategoryEntity = categoryService.partialUpdate(id, categoryMapper.mapFrom(categoryDto));
        return ResponseEntity.ok(categoryMapper.mapTo(updatedCategoryEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
