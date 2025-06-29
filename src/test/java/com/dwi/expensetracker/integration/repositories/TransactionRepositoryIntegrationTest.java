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
import org.springframework.test.context.ActiveProfiles;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.repositories.TransactionRepository;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for TransactionRepository")
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
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category category = categoryRepository.saveAndFlush(TestDataUtil.givenCategoryA(user));
        Transaction transaction = TestDataUtil.givenTransactionA(user, category);

        underTest.saveAndFlush(transaction);
        Optional<Transaction> result = underTest.findById(transaction.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getAmount()).isEqualByComparingTo("30000");
        assertThat(result.get().getUser()).isEqualTo(user);
        assertThat(result.get().getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("2. Should create multiple transactions and retrieve all")
    public void shouldCreateMultipleTransactionsAndRetrieveAll() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category category = categoryRepository.saveAndFlush(TestDataUtil.givenCategoryA(user));

        Transaction transactionA = TestDataUtil.givenTransactionA(user, category);
        Transaction transactionB = TestDataUtil.givenTransactionB(user, category);
        Transaction transactionC = TestDataUtil.givenTransactionC(user, category);

        underTest.saveAllAndFlush(List.of(transactionA, transactionB, transactionC));
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

    @Test
    @DisplayName("4. Should find transactions by user ID")
    public void shouldFindTransactionsByUserId() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category category = categoryRepository.saveAndFlush(TestDataUtil.givenCategoryA(user));
        Transaction transactionA = TestDataUtil.givenTransactionA(user, category);
        Transaction transactionB = TestDataUtil.givenTransactionB(user, category);

        underTest.saveAllAndFlush(List.of(transactionA, transactionB));
        List<Transaction> result = underTest.findByUserId(user.getId());

        assertThat(result)
                .hasSize(2)
                .extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder("Lunch with friends", "Freelance project");
    }

    @Test
    @DisplayName("5. Should return empty list when no transactions exist for user")
    public void shouldReturnEmptyListWhenNoTransactionsForUser() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());

        List<Transaction> result = underTest.findByUserId(user.getId());

        assertThat(result).isEmpty();
    }
}