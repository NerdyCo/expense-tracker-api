package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.enums.TransactionType;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.TransactionService;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for TransactionServiceImpl")
public class TransactionServiceIntegrationTest {

        private final TransactionService underTest;
        private final UserRepository userRepository;
        private final CategoryRepository categoryRepository;

        @Autowired
        public TransactionServiceIntegrationTest(
                        TransactionService underTest,
                        UserRepository userRepository,
                        CategoryRepository categoryRepository) {
                this.underTest = underTest;
                this.userRepository = userRepository;
                this.categoryRepository = categoryRepository;
        }

        @Test
        @DisplayName("1. Should create a transaction and retrieve it successfully")
        public void shouldCreateAndRetrieveTransaction() {
                User user = TestDataUtil.givenUserA();
                userRepository.saveAndFlush(user);
                Category category = TestDataUtil.givenCategoryA(user);
                categoryRepository.saveAndFlush(category);
                Transaction transaction = TestDataUtil.givenTransactionA(user, category);

                Transaction savedTransaction = underTest.create(transaction);
                Transaction foundTransaction = underTest.getById(savedTransaction.getId());

                assertThat(foundTransaction).isNotNull();
                assertThat(foundTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("30000"));
                assertThat(foundTransaction.getType()).isEqualTo(TransactionType.EXPENSE);
                assertThat(foundTransaction.getDescription()).isEqualTo("Lunch with friends");
                assertThat(foundTransaction.getUser().getUsername()).isEqualTo("kautsar");
                assertThat(foundTransaction.getCategory().getName()).isEqualTo("Food & Beverage");
        }

        @Test
        @DisplayName("2. Should create multiple transactions and retrieve all")
        public void shouldCreateMultipleTransactionsAndRetrieveAll() {
                User userA = TestDataUtil.givenUserA();
                User userB = TestDataUtil.givenUserB();
                User userC = TestDataUtil.givenUserC();
                userRepository.saveAllAndFlush(List.of(userA, userB, userC));

                Category categoryA = TestDataUtil.givenCategoryA(userA);
                Category categoryB = TestDataUtil.givenCategoryB(userB);
                Category categoryC = TestDataUtil.givenCategoryC(userC);
                categoryRepository.saveAllAndFlush(List.of(categoryA, categoryB, categoryC));
                underTest.create(TestDataUtil.givenTransactionA(userA, categoryA));
                underTest.create(TestDataUtil.givenTransactionB(userB, categoryB));
                underTest.create(TestDataUtil.givenTransactionC(userC, categoryC));

                Page<Transaction> result = underTest.getAll(PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(3)
                                .extracting(Transaction::getDescription)
                                .containsExactlyInAnyOrder("Lunch with friends", "Freelance project", "Coffee");
        }

        @Test
        @DisplayName("3. Should partially update a transaction")
        public void shouldPartiallyUpdateTransaction() {
                User user = TestDataUtil.givenUserA();
                userRepository.saveAndFlush(user);
                Category category = TestDataUtil.givenCategoryA(user);
                categoryRepository.saveAndFlush(category);
                Transaction savedTransaction = underTest.create(TestDataUtil.givenTransactionA(user, category));

                Transaction updateRequest = Transaction.builder()
                                .description("Dinner with family")
                                .amount(new BigDecimal("50000"))
                                .build();
                Transaction updatedTransaction = underTest.updatePartial(savedTransaction.getId(), updateRequest);

                assertThat(updatedTransaction.getDescription()).isEqualTo("Dinner with family");
                assertThat(updatedTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
                assertThat(updatedTransaction.getUser().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("4. Should delete transaction successfully")
        public void shouldDeleteTransaction() {
                User user = TestDataUtil.givenUserA();
                userRepository.saveAndFlush(user);
                Category category = TestDataUtil.givenCategoryA(user);
                categoryRepository.saveAndFlush(category);
                Transaction savedTransaction = underTest.create(TestDataUtil.givenTransactionA(user, category));

                underTest.deleteById(savedTransaction.getId());

                boolean exists = underTest.getAll(PageRequest.of(0, 10))
                                .stream()
                                .anyMatch(transaction -> transaction.getId().equals(savedTransaction.getId()));
                assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("5. Should return transactions by user ID")
        public void shouldReturnTransactionsByUserId() {
                User user = TestDataUtil.givenUserA();
                userRepository.saveAndFlush(user);
                Category category = TestDataUtil.givenCategoryA(user);
                categoryRepository.saveAndFlush(category);
                Transaction transactionA = underTest.create(TestDataUtil.givenTransactionA(user, category));
                Transaction transactionB = underTest.create(TestDataUtil.givenTransactionB(user, category));

                List<Transaction> result = underTest.getByUserId(user.getId());

                assertThat(result).containsExactlyInAnyOrder(transactionA, transactionB);
        }

        @Test
        @DisplayName("6. Should return empty list when no transactions exist for user")
        public void shouldReturnEmptyListWhenNoTransactions() {
                User user = TestDataUtil.givenUserA();
                userRepository.save(user);

                List<Transaction> result = underTest.getByUserId(user.getId());

                assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("7. Should return empty page when no transactions exist")
        public void shouldReturnEmptyPageWhenNoTransactions() {
                Page<Transaction> result = underTest.getAll(PageRequest.of(0, 10));

                assertThat(result.getContent()).isEmpty();
        }
}