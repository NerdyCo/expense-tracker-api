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
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.domains.entities.TransactionEntity;
import com.dwi.expensetracker.domains.enums.TransactionType;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.CustomerService;
import com.dwi.expensetracker.services.TransactionService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionServiceIntegrationTest {

    private final TransactionService underTest;
    private final CategoryService categoryService;
    private final CustomerService customerService;

    @Autowired
    public TransactionServiceIntegrationTest(
            TransactionService underTest,
            CategoryService categoryService,
            CustomerService customerService) {
        this.underTest = underTest;
        this.categoryService = categoryService;
        this.customerService = customerService;
    }

    @Test
    public void testThatTransactionCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = categoryService.save(TestDataUtil.createTestCategoryEntityA(customer));
        TransactionEntity savedTransaction = underTest
                .save(TestDataUtil.createTestTransactionEntityA(customer, category));

        Optional<TransactionEntity> foundTransaction = underTest.findOne(savedTransaction.getId());

        assertThat(foundTransaction).isPresent();
        assertThat(foundTransaction.get().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(30000));
        assertThat(foundTransaction.get().getType()).isEqualTo(TransactionType.EXPENSE);
    }

    @Test
    public void testThatMultipleTransactionCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = categoryService.save(TestDataUtil.createTestCategoryEntityA(customer));

        TransactionEntity savedTransactionA = underTest
                .save(TestDataUtil.createTestTransactionEntityA(customer, category));
        TransactionEntity savedTransactionB = underTest
                .save(TestDataUtil.createTestTransactionEntityB(customer, category));
        TransactionEntity savedTransactionC = underTest
                .save(TestDataUtil.createTestTransactionEntityC(customer, category));

        Page<TransactionEntity> result = underTest.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .containsExactlyInAnyOrder(savedTransactionA, savedTransactionB, savedTransactionC);
        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    public void testThatTransactionCanBePartiallyUpdated() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = categoryService.save(TestDataUtil.createTestCategoryEntityA(customer));
        TransactionEntity savedTransaction = underTest
                .save(TestDataUtil.createTestTransactionEntityA(customer, category));

        TransactionEntity updateTransaction = TransactionEntity.builder()
                .description("Join party with best friends")
                .build();

        TransactionEntity updatedTransaction = underTest.partialUpdate(savedTransaction.getId(), updateTransaction);

        assertThat(updatedTransaction.getDescription()).isEqualTo("Join party with best friends");
    }

    @Test
    public void testThatTransactionCanBeDeleted() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = categoryService.save(TestDataUtil.createTestCategoryEntityA(customer));
        TransactionEntity savedTransaction = underTest
                .save(TestDataUtil.createTestTransactionEntityA(customer, category));

        underTest.delete(savedTransaction.getId());

        boolean result = underTest.doesExist(savedTransaction.getId());

        assertThat(result).isFalse();
    }
}
