package com.dwi.expensetracker.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.services.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void delete(UUID id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean doesExist(UUID id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Optional<Category> findOne(UUID id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category partialUpdate(UUID id, Category categoryEntity) {
        if (categoryEntity.getCustomer() != null) {
            throw new IllegalArgumentException("Customer cannot be changed");
        }

        categoryEntity.setId(id);

        return categoryRepository.findById(id).map(existingCategory -> {
            Optional.ofNullable(categoryEntity.getName()).ifPresent(existingCategory::setName);
            return categoryRepository.save(existingCategory);
        }).orElseThrow(() -> new RuntimeException("Category does not exist"));
    }

    @Override
    public Category save(Category categoryEntity) {
        return categoryRepository.save(categoryEntity);
    }

}
