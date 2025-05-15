package com.dwi.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.domains.entities.TransactionEntity;
import com.dwi.expensetracker.domains.enums.TransactionType;

public class TestDataUtil {
    // customer
    public static CustomerEntity createTestCustomerEntityA() {
        return CustomerEntity.builder()
                .email("kautsar@gmail.com")
                .username("kautsar")
                .password("kautsar123")
                .build();
    }

    public static CustomerEntity createTestCustomerEntityB() {
        return CustomerEntity.builder()
                .email("teguh@gmail.com")
                .username("teguh")
                .password("teguh123")
                .build();
    }

    public static CustomerEntity createTestCustomerEntityC() {
        return CustomerEntity.builder()
                .email("dwi@gmail.com")
                .username("dwi")
                .password("dwi123")
                .build();
    }

    // category
    public static CategoryEntity createTestCategoryEntityA(final CustomerEntity customer) {
        return CategoryEntity.builder()
                .customer(customer)
                .name("Food & Beverage")
                .build();
    }

    public static CategoryEntity createTestCategoryEntityB(final CustomerEntity customer) {
        return CategoryEntity.builder()
                .customer(customer)
                .name("Transportation")
                .build();
    }

    public static CategoryEntity createTestCategoryEntityC(final CustomerEntity customer) {
        return CategoryEntity.builder()
                .customer(customer)
                .name("Hobby")
                .build();
    }

    // transaction
    public static TransactionEntity createTestTransactionEntityA(
            final CustomerEntity customer,
            final CategoryEntity category) {
        return TransactionEntity.builder()
                .customer(customer)
                .category(category)
                .amount(new BigDecimal("30000"))
                .type(TransactionType.EXPENSE)
                .description("Lunch with friends")
                .date(LocalDate.of(2025, 5, 1))
                .build();
    }

    public static TransactionEntity createTestTransactionEntityB(
            final CustomerEntity customer,
            final CategoryEntity category) {
        return TransactionEntity.builder()
                .customer(customer)
                .category(category)
                .amount(new BigDecimal("100000"))
                .type(TransactionType.INCOME)
                .description("Freelance project")
                .date(LocalDate.of(2025, 5, 3))
                .build();
    }

    public static TransactionEntity createTestTransactionEntityC(
            final CustomerEntity customer,
            final CategoryEntity category) {
        return TransactionEntity.builder()
                .customer(customer)
                .category(category)
                .amount(new BigDecimal("5000"))
                .type(TransactionType.EXPENSE)
                .description("Coffee")
                .date(LocalDate.of(2025, 5, 5))
                .build();
    }
}
