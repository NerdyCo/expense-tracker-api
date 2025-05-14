package com.dwi.expensetracker.repositories;

import org.springframework.data.repository.CrudRepository;

import com.dwi.expensetracker.domains.entities.CategoryEntity;

public interface CategoryRepository extends CrudRepository<CategoryEntity, Long> {

}
