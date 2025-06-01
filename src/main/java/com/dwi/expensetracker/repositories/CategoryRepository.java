package com.dwi.expensetracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dwi.expensetracker.domains.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
