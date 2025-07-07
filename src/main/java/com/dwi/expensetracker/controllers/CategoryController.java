package com.dwi.expensetracker.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.dwi.expensetracker.mappers.impl.category.CategoryBaseMapper;
import com.dwi.expensetracker.mappers.impl.category.CategoryPatchMapper;
import com.dwi.expensetracker.mappers.impl.category.CategoryRequestMapper;
import com.dwi.expensetracker.services.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryBaseMapper categoryBaseMapper;
    private final CategoryRequestMapper categoryRequestMapper;
    private final CategoryPatchMapper categoryPatchMapper;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryBaseDto> createCategory(@Valid @RequestBody CategoryRequestDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!userId.equals(requestDto.getUserId())) {
            throw new IllegalStateException("You can only create categories for yourself");
        }

        Category categoryTocreate = categoryRequestMapper.toEntity(requestDto);
        Category createdCategory = categoryService.create(categoryTocreate);
        CategoryBaseDto responseDto = categoryBaseMapper.toDto(createdCategory);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CategoryBaseDto>> getAllCategories(Pageable pageable) {
        Page<CategoryBaseDto> categories = categoryService.getAll(pageable).map(categoryBaseMapper::toDto);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<CategoryBaseDto> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getById(id);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        // only allow user to view their own categories or admin to view all categories
        if (!category.getUser().getId().equals(userId) &&
                !SecurityContextHolder
                        .getContext().getAuthentication()
                        .getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new IllegalStateException("You can only view your own categories");
        }

        return ResponseEntity.ok(categoryBaseMapper.toDto(category));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CategoryBaseDto> updateCategoryPartially(
            @PathVariable UUID id,
            @Valid @RequestBody CategoryPatchDto patchDto) {
        Category category = categoryService.getById(id);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only update your own categories");
        }

        categoryPatchMapper.updateCategoryFromDto(patchDto, category);

        Category updatedCategory = categoryService.updatePartial(id, category);

        return ResponseEntity.ok(categoryBaseMapper.toDto(updatedCategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        Category category = categoryService.getById(id);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!category.getUser().getId().equals(userId)) {
            throw new IllegalStateException("You can only delete your own categories");
        }

        categoryService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
