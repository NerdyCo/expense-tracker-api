package com.dwi.expensetracker.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Category;

public interface CategoryService {
    Category create(Category category);

    Page<Category> getAll(Pageable pageable);

    Category getById(UUID id);

    List<Category> getByUserId(String userId);

    Category updatePartial(UUID id, Category category);

    void deleteById(UUID id);
}
