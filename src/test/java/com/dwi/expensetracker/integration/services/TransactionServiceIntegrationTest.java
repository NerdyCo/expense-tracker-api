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
import org.springframework.test.annotation.DirtiesContext;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.enums.TransactionType;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.TransactionService;
import com.dwi.expensetracker.services.UserService;

import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TransactionServiceIntegrationTest {

        private final TransactionService underTest;
        private final UserService userService;
        private final CategoryService categoryService;

        @Autowired
        public TransactionServiceIntegrationTest(
                        TransactionService underTest,
                        UserService userService,
                        CategoryService categoryService) {
                this.underTest = underTest;
                this.userService = userService;
                this.categoryService = categoryService;
        }

        @Test
        @Transactional
        @DisplayName("1. Should create a transaction and retrieve it successfully")
        public void shouldCreateAndRetrieveTransaction() {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                Transaction transaction = TestDataUtil.givenTransactionA(user, category);

                Transaction savedTransaction = underTest.create(transaction);
                Transaction foundTransaction = underTest.getById(savedTransaction.getId());

                assertThat(foundTransaction).isNotNull();
                assertThat(foundTransaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(30000));
                assertThat(foundTransaction.getType()).isEqualTo(TransactionType.EXPENSE);
                assertThat(foundTransaction.getDescription()).isEqualTo("Lunch with friends");
                assertThat(foundTransaction.getUser().getUsername()).isEqualTo("kautsar");
                assertThat(foundTransaction.getCategory().getName()).isEqualTo("Food & Beverage");
        }

        @Test
        @DisplayName("2. Should create multiple transactions and retrieve all")
        public void shouldCreateMultipleTransactionsAndRetrieveAll() {
                User userA = userService.create(TestDataUtil.givenUserA());
                User userB = userService.create(TestDataUtil.givenUserB());
                User userC = userService.create(TestDataUtil.givenUserC());

                Category categoryA = categoryService.create(TestDataUtil.givenCategoryA(userA));
                Category categoryB = categoryService.create(TestDataUtil.givenCategoryB(userB));
                Category categoryC = categoryService.create(TestDataUtil.givenCategoryC(userC));

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
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                Transaction savedTransaction = underTest.create(TestDataUtil.givenTransactionA(user, category));

                Transaction updateRequest = Transaction.builder()
                                .description("Dinner with family")
                                .amount(BigDecimal.valueOf(50000))
                                .build();

                Transaction updatedTransaction = underTest.updatePartial(savedTransaction.getId(), updateRequest);

                assertThat(updatedTransaction.getDescription()).isEqualTo("Dinner with family");
                assertThat(updatedTransaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
                assertThat(updatedTransaction.getUser().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("4. Should delete transaction successfully")
        public void shouldDeleteTransaction() {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
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
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                Transaction transactionA = underTest.create(TestDataUtil.givenTransactionA(user, category));
                Transaction transactionB = underTest.create(TestDataUtil.givenTransactionB(user, category));

                List<Transaction> result = underTest.getByUserId(user.getId());

                assertThat(result).containsExactlyInAnyOrder(transactionA, transactionB);
        }
}
