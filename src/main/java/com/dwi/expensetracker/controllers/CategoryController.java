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

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final Mapper<Category, CategoryBaseDto> categoryMapper;
    private final Mapper<Category, CategoryRequestDto> createCategoryMapper;

    @PostMapping
    public ResponseEntity<CategoryRequestDto> createCategory(@RequestBody CategoryRequestDto createCategoryDto) {
        Category categoryEntity = createCategoryMapper.mapFrom(createCategoryDto);
        Category savedCategoryEntity = categoryService.save(categoryEntity);
        CategoryRequestDto savedDto = createCategoryMapper.mapTo(savedCategoryEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryBaseDto> getCategory(@PathVariable Long id) {
        return categoryService.findOne(id)
                .map(categoryMapper::mapTo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<CategoryBaseDto> getAllCategories(Pageable pageable) {
        return categoryService.findAll(pageable)
                .map(categoryMapper::mapTo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryBaseDto> fullUpdateCategory(
            @PathVariable Long id,
            @RequestBody CategoryBaseDto categoryDto) {
        if (!categoryService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        categoryDto.setId(id);
        Category updatedCategoryEntity = categoryService.save(categoryMapper.mapFrom(categoryDto));
        return ResponseEntity.ok(categoryMapper.mapTo(updatedCategoryEntity));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryBaseDto> partialUpdateCategory(
            @PathVariable Long id,
            @RequestBody CategoryBaseDto categoryDto) {
        if (!categoryService.doesExist(id)) {
            return ResponseEntity.notFound().build();
        }

        Category updatedCategoryEntity = categoryService.partialUpdate(id, categoryMapper.mapFrom(categoryDto));
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
