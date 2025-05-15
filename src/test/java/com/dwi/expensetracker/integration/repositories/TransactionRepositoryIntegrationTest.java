package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.domains.entities.TransactionEntity;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.CustomerRepository;
import com.dwi.expensetracker.repositories.TransactionRepository;

@DataJpaTest
public class TransactionRepositoryIntegrationTest {
    private final TransactionRepository underTest;
    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TransactionRepositoryIntegrationTest(
            TransactionRepository underTest,
            CustomerRepository customerRepository,
            CategoryRepository categoryRepository) {
        this.underTest = underTest;
        this.customerRepository = customerRepository;
        this.categoryRepository = categoryRepository;
    }

    @Test
    public void testThatTransactionCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerRepository.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA(customer));
        TransactionEntity transaction = TestDataUtil.createTestTransactionEntityA(customer, category);

        underTest.save(transaction);

        Optional<TransactionEntity> result = underTest.findById(transaction.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo("30000");
        assertThat(result.get().getCustomer()).isEqualTo(customer);
        assertThat(result.get().getCategory()).isEqualTo(category);
    }

    @Test
    public void testThatMultipleTransactionsCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerRepository.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = categoryRepository.save(TestDataUtil.createTestCategoryEntityA(customer));

        TransactionEntity transactionA = TestDataUtil.createTestTransactionEntityA(customer, category);
        TransactionEntity transactionB = TestDataUtil.createTestTransactionEntityB(customer, category);
        TransactionEntity transactionC = TestDataUtil.createTestTransactionEntityC(customer, category);

        underTest.saveAll(List.of(transactionA, transactionB, transactionC));

        Iterable<TransactionEntity> result = underTest.findAll();

        assertThat(result).hasSize(3).containsExactly(transactionA, transactionB, transactionC);
    }
}
