package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.repositories.TransactionRepository;

@DataJpaTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TransactionRepositoryIntegrationTest {
    private final TransactionRepository underTest;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public TransactionRepositoryIntegrationTest(TransactionRepository underTest, UserRepository userRepository,
            CategoryRepository categoryRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Test
    @DisplayName("1. Should create and retrieve a transaction by ID")
    public void shouldCreateAndRetrieveTransactionById() {
        User user = userRepository.save(TestDataUtil.givenUserA());
        Category category = categoryRepository.save(TestDataUtil.givenCategoryA(user));
        Transaction transaction = TestDataUtil.givenTransactionA(user, category);

        underTest.save(transaction);
        Optional<Transaction> result = underTest.findById(transaction.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo("30000");
        assertThat(result.get().getUser()).isEqualTo(user);
        assertThat(result.get().getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("2. Should create multiple transactions and retrieve all")
    public void shouldCreateMultipleTransactionsAndRetrieveAll() {
        User user = userRepository.save(TestDataUtil.givenUserA());
        Category category = categoryRepository.save(TestDataUtil.givenCategoryA(user));

        Transaction transactionA = TestDataUtil.givenTransactionA(user, category);
        Transaction transactionB = TestDataUtil.givenTransactionB(user, category);
        Transaction transactionC = TestDataUtil.givenTransactionC(user, category);

        underTest.saveAll(List.of(transactionA, transactionB, transactionC));
        List<Transaction> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(transactionA, transactionB, transactionC);
    }

    @Test
    @DisplayName("3. Should return empty if transaction not found by ID")
    public void shouldReturnEmptyIfTransactionNotFoundById() {
        Optional<Transaction> result = underTest.findById(UUID.randomUUID());

        assertThat(result).isNotPresent();
    }
}
