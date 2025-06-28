package com.dwi.expensetracker.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.User;

public interface UserService {
    Page<User> getAll(Pageable pageable);

    User getById(String id);
}
