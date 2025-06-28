package com.dwi.expensetracker.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.exceptions.DuplicateResourceException;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.CategoryService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Category create(Category category) {
        String userId = category.getUser().getId();

        if (categoryRepository.existsByUserIdAndName(userId, category.getName())) {
            throw new DuplicateResourceException("Category name already exists for this user");
        }

        return categoryRepository.save(category);
    }

    @Override
    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Category getById(UUID id) {
        return categoryRepository.findByIdWithUser(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + id));
    }

    @Override
    @Transactional
    public Category updatePartial(UUID id, Category category) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    // prevent changing the user (foreign key)
                    if (category.getUser() != null && !category.getUser().getId().equals(existing.getUser().getId())) {
                        throw new IllegalArgumentException("User cannot be changed for a category");
                    }

                    // handle uniqueness name check
                    if (category.getName() != null && !category.getName().equals(existing.getName())) {
                        if (categoryRepository.existsByUserIdAndName(existing.getUser().getId(), category.getName())) {
                            throw new DuplicateResourceException("Category name already exists for this user");
                        }

                        existing.setName(category.getName());
                    }

                    return categoryRepository.save(existing);
                }).orElseThrow(() -> new EntityNotFoundException("Category not found with ID " + id));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found with ID " + id);
        }

        categoryRepository.deleteById(id);
    }

    @Override
    public List<Category> getByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with ID " + userId);
        }

        return categoryRepository.findByUserId(userId);
    }

}
