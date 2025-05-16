package com.dwi.expensetracker.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.repositories.CustomerRepository;
import com.dwi.expensetracker.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public boolean doesExist(Long id) {
        return customerRepository.existsById(id);
    }

    @Override
    public Page<CustomerEntity> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    public Optional<CustomerEntity> findOne(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public CustomerEntity partialUpdate(Long id, CustomerEntity customerEntity) {
        customerEntity.setId(id);

        return customerRepository.findById(id).map(existingCustomer -> {
            Optional.ofNullable(customerEntity.getUsername()).ifPresent(existingCustomer::setUsername);
            Optional.ofNullable(customerEntity.getEmail()).ifPresent(existingCustomer::setEmail);
            Optional.ofNullable(customerEntity.getPassword()).ifPresent(rawPassword -> {
                String hashedPassword = passwordEncoder.encode(rawPassword);
                existingCustomer.setPassword(hashedPassword);
            });
            return customerRepository.save(existingCustomer);
        }).orElseThrow(() -> new RuntimeException("Customer does not exist"));
    }

    @Override
    public CustomerEntity save(CustomerEntity customerEntity) {
        if (customerEntity.getPassword() != null) {
            String hashedPassword = passwordEncoder.encode(customerEntity.getPassword());
            customerEntity.setPassword(hashedPassword);
        }

        return customerRepository.save(customerEntity);
    }

}
