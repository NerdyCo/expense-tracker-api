package com.dwi.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dwi.expensetracker.domains.dtos.category.CategoryDto;
import com.dwi.expensetracker.domains.dtos.category.CreateCategoryDto;
import com.dwi.expensetracker.domains.dtos.customer.CreateCustomerDto;
import com.dwi.expensetracker.domains.dtos.customer.CustomerDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionDto;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.domains.entities.TransactionEntity;
import com.dwi.expensetracker.domains.enums.TransactionType;

public class TestDataUtil {
        // customer
        // entity
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

        // dto
        public static CreateCustomerDto createTestCustomerDtoA() {
                return CreateCustomerDto.builder()
                                .email("kautsar@gmail.com")
                                .username("kautsar")
                                .password("kautsar123")
                                .build();
        }

        public static CreateCustomerDto createTestCustomerDtoB() {
                return CreateCustomerDto.builder()
                                .email("teguh@gmail.com")
                                .username("teguh")
                                .password("teguh123")
                                .build();
        }

        public static CreateCustomerDto createTestCustomerDtoC() {
                return CreateCustomerDto.builder()
                                .email("dwi@gmail.com")
                                .username("dwi")
                                .password("dwi123")
                                .build();
        }

        // category
        // entity
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

        // dto
        public static CreateCategoryDto createTestCategoryDtoA(final CreateCustomerDto customer) {
                return CreateCategoryDto.builder()
                                .customer(customer)
                                .name("Food & Beverage")
                                .build();
        }

        public static CreateCategoryDto createTestCategoryDtoB(final CreateCustomerDto customer) {
                return CreateCategoryDto.builder()
                                .customer(customer)
                                .name("Transportation")
                                .build();
        }

        public static CreateCategoryDto createTestCategoryDtoC(final CreateCustomerDto customer) {
                return CreateCategoryDto.builder()
                                .customer(customer)
                                .name("Hobby")
                                .build();
        }

        // transaction
        // entity
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

        // dto
        public static TransactionDto createTestTransactionDtoA(
                        final CustomerDto customer,
                        final CategoryDto category) {
                return TransactionDto.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("30000"))
                                .type(TransactionType.EXPENSE)
                                .description("Lunch with friends")
                                .date(LocalDate.of(2025, 5, 1))
                                .build();
        }

        public static TransactionDto createTestTransactionDtoB(
                        final CustomerDto customer,
                        final CategoryDto category) {
                return TransactionDto.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("100000"))
                                .type(TransactionType.INCOME)
                                .description("Freelance project")
                                .date(LocalDate.of(2025, 5, 3))
                                .build();
        }

        public static TransactionDto createTestTransactionDtoC(
                        final CustomerDto customer,
                        final CategoryDto category) {
                return TransactionDto.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("5000"))
                                .type(TransactionType.EXPENSE)
                                .description("Coffee")
                                .date(LocalDate.of(2025, 5, 5))
                                .build();
        }
}
