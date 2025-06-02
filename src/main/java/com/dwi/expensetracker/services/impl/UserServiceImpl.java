package com.dwi.expensetracker.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean doesExist(UUID id) {
        return userRepository.existsById(id);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> findOne(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public User partialUpdate(UUID id, User customerEntity) {
        customerEntity.setId(id);

        return userRepository.findById(id).map(existingCustomer -> {
            Optional.ofNullable(customerEntity.getUsername()).ifPresent(existingCustomer::setUsername);
            Optional.ofNullable(customerEntity.getEmail()).ifPresent(existingCustomer::setEmail);
            Optional.ofNullable(customerEntity.getPassword()).ifPresent(rawPassword -> {
                String hashedPassword = passwordEncoder.encode(rawPassword);
                existingCustomer.setPassword(hashedPassword);
            });
            return userRepository.save(existingCustomer);
        }).orElseThrow(() -> new RuntimeException("Customer does not exist"));
    }

    @Override
    public User save(User customerEntity) {
        if (customerEntity.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(customerEntity.getPassword());
            customerEntity.setPassword(hashedPassword);
        }

        return userRepository.save(customerEntity);
    }

}
