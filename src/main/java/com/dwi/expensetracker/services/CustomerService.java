package com.dwi.expensetracker.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.dwi.expensetracker.domains.entities.Customer;

public interface CustomerService {
    Customer save(Customer customerEntity);

    Page<Customer> findAll(Pageable pageable);

    Optional<Customer> findOne(Long id);

    boolean doesExist(Long id);

    Customer partialUpdate(Long id, Customer customerEntity);

    void delete(Long id);
}
