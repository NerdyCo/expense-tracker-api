package com.dwi.expensetracker.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.exceptions.DuplicateResourceException;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User create(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email is already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("Username is already in use");
        }

        String rawPassword = user.getPassword();
        if (rawPassword != null && !rawPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }

        return userRepository.save(user);
    }

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id));
    }

    @Override
    @Transactional
    public User updatePartial(UUID id, User updateRequest) {
        return userRepository.findById(id)
                .map(existing -> {
                    Optional.ofNullable(updateRequest.getUsername()).ifPresent(existing::setUsername);
                    Optional.ofNullable(updateRequest.getEmail()).ifPresent(existing::setEmail);
                    Optional.ofNullable(updateRequest.getPassword())
                            .filter(pwd -> !pwd.isBlank())
                            .map(passwordEncoder::encode)
                            .ifPresent(existing::setPassword);

                    return userRepository.save(existing);
                }).orElseThrow(() -> new EntityNotFoundException("User not found with ID " + id));
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with ID " + id);
        }

        userRepository.deleteById(id);
    }
}
