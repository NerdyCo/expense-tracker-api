package com.dwi.expensetracker.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dwi.expensetracker.domains.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    boolean existsByUserIdAndName(UUID userId, String name);

    @Query("SELECT c FROM Category c JOIN FETCH c.user WHERE c.id = :id")
    Optional<Category> findByIdWithUser(@Param("id") UUID id);

    List<Category> findByUserId(UUID userId);
}
