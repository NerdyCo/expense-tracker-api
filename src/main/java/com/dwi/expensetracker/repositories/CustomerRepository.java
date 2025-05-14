package com.dwi.expensetracker.repositories;

import org.springframework.data.repository.CrudRepository;

import com.dwi.expensetracker.domains.entities.CustomerEntity;

public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

}
