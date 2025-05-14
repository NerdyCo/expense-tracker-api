package com.dwi.expensetracker.repositories;

import org.springframework.data.repository.CrudRepository;

import com.dwi.expensetracker.domains.entities.TransactionEntity;

public interface TransactionRepository extends CrudRepository<TransactionEntity, Long> {

}
