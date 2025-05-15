package com.dwi.expensetracker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.CustomerEntity;

public interface CustomerService {
    CustomerEntity save(CustomerEntity customerEntity);

    Page<CustomerEntity> findAll(Pageable pageable);

    Optional<CustomerEntity> findOne(Long id);

    boolean doesExist(Long id);

    CustomerEntity partialUpdate(Long id, CustomerEntity customerEntity);

    void delete(Long id);
}
