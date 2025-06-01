package com.dwi.expensetracker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dwi.expensetracker.domains.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
