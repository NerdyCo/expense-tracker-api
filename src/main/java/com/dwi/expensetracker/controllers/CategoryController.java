package com.dwi.expensetracker.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryPatchDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.mappers.Mapper;
import com.dwi.expensetracker.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final Mapper<Category, CategoryBaseDto> categoryBaseMapper;
    private final Mapper<Category, CategoryRequestDto> categoryRequestMapper;
    private final Mapper<Category, CategoryPatchDto> categoryPatchMapper;

    @PostMapping
    public ResponseEntity<CategoryBaseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        Category categoryTocreate = categoryRequestMapper.toEntity(requestDto);
        Category createdCategory = categoryService.create(categoryTocreate);
        CategoryBaseDto responseDto = categoryBaseMapper.toDto(createdCategory);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryBaseDto>> getAllCategories(Pageable pageable) {
        Page<CategoryBaseDto> categories = categoryService.getAll(pageable).map(categoryBaseMapper::toDto);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryBaseDto> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getById(id);

        return ResponseEntity.ok(categoryBaseMapper.toDto(category));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryBaseDto> updateCategoryPartially(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryPatchDto patchDto) {
        Category patchRequest = categoryPatchMapper.toEntity(patchDto);
        Category updatedCategory = categoryService.updatePartial(id, patchRequest);

        return ResponseEntity.ok(categoryBaseMapper.toDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
