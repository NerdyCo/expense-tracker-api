package com.dwi.expensetracker.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Category;

public interface CategoryService {
    Category save(Category categoryEntity);

    Page<Category> findAll(Pageable pageable);

    Optional<Category> findOne(UUID id);

    boolean doesExist(UUID id);

    Category partialUpdate(UUID id, Category categoryEntity);

    void delete(UUID id);
}
