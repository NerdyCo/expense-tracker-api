package com.dwi.expensetracker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Category;

public interface CategoryService {
    Category save(Category categoryEntity);

    Page<Category> findAll(Pageable pageable);

    Optional<Category> findOne(Long id);

    boolean doesExist(Long id);

    Category partialUpdate(Long id, Category categoryEntity);

    void delete(Long id);
}
