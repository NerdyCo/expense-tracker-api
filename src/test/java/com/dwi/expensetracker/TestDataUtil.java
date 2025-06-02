package com.dwi.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.enums.TransactionType;

public class TestDataUtil {
        // customer
        // entity
        public static User createTestCustomerEntityA() {
                return User.builder()
                                .email("kautsar@gmail.com")
                                .username("kautsar")
                                .password("kautsar123")
                                .build();
        }

        public static User createTestCustomerEntityB() {
                return User.builder()
                                .email("teguh@gmail.com")
                                .username("teguh")
                                .password("teguh123")
                                .build();
        }

        public static User createTestCustomerEntityC() {
                return User.builder()
                                .email("dwi@gmail.com")
                                .username("dwi")
                                .password("dwi123")
                                .build();
        }

        // dto
        public static UserRequestDto createTestCustomerDtoA() {
                return UserRequestDto.builder()
                                .email("kautsar@gmail.com")
                                .username("kautsar")
                                .password("kautsar123")
                                .build();
        }

        public static UserRequestDto createTestCustomerDtoB() {
                return UserRequestDto.builder()
                                .email("teguh@gmail.com")
                                .username("teguh")
                                .password("teguh123")
                                .build();
        }

        public static UserRequestDto createTestCustomerDtoC() {
                return UserRequestDto.builder()
                                .email("dwi@gmail.com")
                                .username("dwi")
                                .password("dwi123")
                                .build();
        }

        // category
        // entity
        public static Category createTestCategoryEntityA(final User customer) {
                return Category.builder()
                                .customer(customer)
                                .name("Food & Beverage")
                                .build();
        }

        public static Category createTestCategoryEntityB(final User customer) {
                return Category.builder()
                                .customer(customer)
                                .name("Transportation")
                                .build();
        }

        public static Category createTestCategoryEntityC(final User customer) {
                return Category.builder()
                                .customer(customer)
                                .name("Hobby")
                                .build();
        }

        // dto
        public static CategoryRequestDto createTestCategoryDtoA(final UserRequestDto customer) {
                return CategoryRequestDto.builder()
                                .customer(customer)
                                .name("Food & Beverage")
                                .build();
        }

        public static CategoryRequestDto createTestCategoryDtoB(final UserRequestDto customer) {
                return CategoryRequestDto.builder()
                                .customer(customer)
                                .name("Transportation")
                                .build();
        }

        public static CategoryRequestDto createTestCategoryDtoC(final UserRequestDto customer) {
                return CategoryRequestDto.builder()
                                .customer(customer)
                                .name("Hobby")
                                .build();
        }

        // transaction
        // entity
        public static Transaction createTestTransactionEntityA(
                        final User customer,
                        final Category category) {
                return Transaction.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("30000"))
                                .type(TransactionType.EXPENSE)
                                .description("Lunch with friends")
                                .date(LocalDate.of(2025, 5, 1))
                                .build();
        }

        public static Transaction createTestTransactionEntityB(
                        final User customer,
                        final Category category) {
                return Transaction.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("100000"))
                                .type(TransactionType.INCOME)
                                .description("Freelance project")
                                .date(LocalDate.of(2025, 5, 3))
                                .build();
        }

        public static Transaction createTestTransactionEntityC(
                        final User customer,
                        final Category category) {
                return Transaction.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("5000"))
                                .type(TransactionType.EXPENSE)
                                .description("Coffee")
                                .date(LocalDate.of(2025, 5, 5))
                                .build();
        }

        // dto
        public static TransactionBaseDto createTestTransactionDtoA(
                        final UserBaseDto customer,
                        final CategoryBaseDto category) {
                return TransactionBaseDto.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("30000"))
                                .type(TransactionType.EXPENSE)
                                .description("Lunch with friends")
                                .date(LocalDate.of(2025, 5, 1))
                                .build();
        }

        public static TransactionBaseDto createTestTransactionDtoB(
                        final UserBaseDto customer,
                        final CategoryBaseDto category) {
                return TransactionBaseDto.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("100000"))
                                .type(TransactionType.INCOME)
                                .description("Freelance project")
                                .date(LocalDate.of(2025, 5, 3))
                                .build();
        }

        public static TransactionBaseDto createTestTransactionDtoC(
                        final UserBaseDto customer,
                        final CategoryBaseDto category) {
                return TransactionBaseDto.builder()
                                .customer(customer)
                                .category(category)
                                .amount(new BigDecimal("5000"))
                                .type(TransactionType.EXPENSE)
                                .description("Coffee")
                                .date(LocalDate.of(2025, 5, 5))
                                .build();
        }
}
