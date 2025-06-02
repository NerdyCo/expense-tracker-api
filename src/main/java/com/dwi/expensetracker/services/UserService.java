package com.dwi.expensetracker.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.User;

public interface UserService {
    User create(User user);

    Page<User> getAll(Pageable pageable);

    Optional<User> getById(UUID id);

    User updatePartial(UUID id, User user);

    void deleteById(UUID id);
}
