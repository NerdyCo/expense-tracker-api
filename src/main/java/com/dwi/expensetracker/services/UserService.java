package com.dwi.expensetracker.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.User;

public interface UserService {
    User save(User customerEntity);

    Page<User> findAll(Pageable pageable);

    Optional<User> findOne(UUID id);

    boolean doesExist(UUID id);

    User partialUpdate(UUID id, User customerEntity);

    void delete(UUID id);
}
