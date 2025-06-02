package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.enums.TransactionType;
import com.dwi.expensetracker.services.TransactionService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionServiceIntegrationTest {

        private final TransactionService underTest;

        @Autowired
        public TransactionServiceIntegrationTest(TransactionService underTest) {
                this.underTest = underTest;
        }

        @Test
        public void testThatTransactionCanBeCreatedAndRecalled() {
                User customer = TestDataUtil.createTestCustomerEntityA();
                Category category = TestDataUtil.createTestCategoryEntityA(customer);
                Transaction savedTransaction = underTest
                                .save(TestDataUtil.createTestTransactionEntityA(customer, category));

                Optional<Transaction> foundTransaction = underTest.findOne(savedTransaction.getId());

                assertThat(foundTransaction).isPresent();
                assertThat(foundTransaction.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(30000));
                assertThat(foundTransaction.get().getType()).isEqualTo(TransactionType.EXPENSE);
        }

        @Test
        public void testThatMultipleTransactionCanBeCreatedAndRecalled() {
                User customerA = TestDataUtil.createTestCustomerEntityA();
                User customerB = TestDataUtil.createTestCustomerEntityB();
                User customerC = TestDataUtil.createTestCustomerEntityC();
                Category categoryA = TestDataUtil.createTestCategoryEntityA(customerA);
                Category categoryB = TestDataUtil.createTestCategoryEntityB(customerB);
                Category categoryC = TestDataUtil.createTestCategoryEntityC(customerC);

                underTest.save(TestDataUtil.createTestTransactionEntityA(customerA, categoryA));
                underTest.save(TestDataUtil.createTestTransactionEntityB(customerB, categoryB));
                underTest.save(TestDataUtil.createTestTransactionEntityC(customerC, categoryC));

                Page<Transaction> result = underTest.findAll(PageRequest.of(0, 10));

                assertThat(result.getContent())
                                .hasSize(3)
                                .extracting(Transaction::getDescription)
                                .containsExactlyInAnyOrder("Lunch with friends", "Freelance project", "Coffee");
                assertThat(result.getTotalElements()).isEqualTo(3);
        }

        @Test
        public void testThatTransactionCanBePartiallyUpdated() {
                User customer = TestDataUtil.createTestCustomerEntityA();
                Category category = TestDataUtil.createTestCategoryEntityA(customer);
                Transaction savedTransaction = underTest
                                .save(TestDataUtil.createTestTransactionEntityA(customer, category));

                Transaction updateTransaction = Transaction.builder()
                                .description("Join party with best friends")
                                .build();

                Transaction updatedTransaction = underTest.partialUpdate(savedTransaction.getId(),
                                updateTransaction);

                assertThat(updatedTransaction.getDescription()).isEqualTo("Join party with best friends");
        }

        @Test
        public void testThatTransactionCanBeDeleted() {
                User customer = TestDataUtil.createTestCustomerEntityA();
                Category category = TestDataUtil.createTestCategoryEntityA(customer);
                Transaction savedTransaction = underTest
                                .save(TestDataUtil.createTestTransactionEntityA(customer, category));

                underTest.delete(savedTransaction.getId());

                boolean result = underTest.doesExist(savedTransaction.getId());

                assertThat(result).isFalse();
        }
}
