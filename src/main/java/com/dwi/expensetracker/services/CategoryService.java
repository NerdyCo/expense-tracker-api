package com.dwi.expensetracker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.CategoryEntity;

public interface CategoryService {
    CategoryEntity save(CategoryEntity categoryEntity);

    Page<CategoryEntity> findAll(Pageable pageable);

    Optional<CategoryEntity> findOne(Long id);

    boolean doesExist(Long id);

    CategoryEntity partialUpdate(Long id, CategoryEntity categoryEntity);

    void delete(Long id);
}
