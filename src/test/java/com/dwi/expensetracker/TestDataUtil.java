package com.dwi.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.enums.TransactionType;

public class TestDataUtil {
        // === constants ===
        private static final String EMAIL_A = "kautsar@gmail.com";
        private static final String EMAIL_B = "teguh@gmail.com";
        private static final String EMAIL_C = "dwi@gmail.com";

        private static final String USERNAME_A = "kautsar";
        private static final String USERNAME_B = "teguh";
        private static final String USERNAME_C = "dwi";

        private static final String PASSWORD = "password123";

        private static final BigDecimal AMOUNT_A = new BigDecimal("30000");
        private static final BigDecimal AMOUNT_B = new BigDecimal("100000");
        private static final BigDecimal AMOUNT_C = new BigDecimal("5000");

        private static final LocalDate DATE_A = LocalDate.of(2025, 5, 1);
        private static final LocalDate DATE_B = LocalDate.of(2025, 5, 3);
        private static final LocalDate DATE_C = LocalDate.of(2025, 5, 5);

        // === USER ===
        public static User givenUserA() {
                return createUser(EMAIL_A, USERNAME_A, PASSWORD);
        }

        public static User givenUserB() {
                return createUser(EMAIL_B, USERNAME_B, PASSWORD);
        }

        public static User givenUserC() {
                return createUser(EMAIL_C, USERNAME_C, PASSWORD);
        }

        public static UserRequestDto givenUserRequestDtoA() {
                return createUserRequestDto(EMAIL_A, USERNAME_A, PASSWORD);
        }

        public static UserRequestDto givenUserRequestDtoB() {
                return createUserRequestDto(EMAIL_B, USERNAME_B, PASSWORD);
        }

        public static UserRequestDto givenUserRequestDtoC() {
                return createUserRequestDto(EMAIL_C, USERNAME_C, PASSWORD);
        }

        private static User createUser(String email, String username, String password) {
                return User.builder()
                                .email(email)
                                .username(username)
                                .password(password)
                                .build();
        }

        private static UserRequestDto createUserRequestDto(String email, String username, String password) {
                return UserRequestDto.builder()
                                .email(email)
                                .username(username)
                                .password(password)
                                .build();
        }

        // === CATEGORY ===
        public static Category givenCategoryA(User user) {
                return createCategory(user, "Food & Beverage");
        }

        public static Category givenCategoryB(User user) {
                return createCategory(user, "Transportation");
        }

        public static Category givenCategoryC(User user) {
                return createCategory(user, "Hobby");
        }

        public static CategoryRequestDto givenCategoryDtoA(UUID userId) {
                return createCategoryDto(userId, "Food & Beverage");
        }

        public static CategoryRequestDto givenCategoryDtoB(UUID userId) {
                return createCategoryDto(userId, "Transportation");
        }

        public static CategoryRequestDto givenCategoryDtoC(UUID userId) {
                return createCategoryDto(userId, "Hobby");
        }

        private static Category createCategory(User user, String name) {
                return Category.builder()
                                .user(user)
                                .name(name)
                                .build();
        }

        private static CategoryRequestDto createCategoryDto(UUID userId, String name) {
                return CategoryRequestDto.builder()
                                .userId(userId)
                                .name(name)
                                .build();
        }

        // === TRANSACTION ===
        public static Transaction givenTransactionA(User user, Category category) {
                return createTransaction(user, category, AMOUNT_A, TransactionType.EXPENSE, "Lunch with friends",
                                DATE_A);
        }

        public static Transaction givenTransactionB(User user, Category category) {
                return createTransaction(user, category, AMOUNT_B, TransactionType.INCOME, "Freelance project", DATE_B);
        }

        public static Transaction givenTransactionC(User user, Category category) {
                return createTransaction(user, category, AMOUNT_C, TransactionType.EXPENSE, "Coffee", DATE_C);
        }

        public static TransactionBaseDto givenTransactionDtoA(UserBaseDto user, CategoryBaseDto category) {
                return createTransactionDto(user, category, AMOUNT_A, TransactionType.EXPENSE, "Lunch with friends",
                                DATE_A);
        }

        public static TransactionBaseDto givenTransactionDtoB(UserBaseDto user, CategoryBaseDto category) {
                return createTransactionDto(user, category, AMOUNT_B, TransactionType.INCOME, "Freelance project",
                                DATE_B);
        }

        public static TransactionBaseDto givenTransactionDtoC(UserBaseDto user, CategoryBaseDto category) {
                return createTransactionDto(user, category, AMOUNT_C, TransactionType.EXPENSE, "Coffee", DATE_C);
        }

        private static Transaction createTransaction(User user, Category category, BigDecimal amount,
                        TransactionType type, String description, LocalDate date) {
                return Transaction.builder()
                                .user(user)
                                .category(category)
                                .amount(amount)
                                .type(type)
                                .description(description)
                                .date(date)
                                .build();
        }

        private static TransactionBaseDto createTransactionDto(UserBaseDto user, CategoryBaseDto category,
                        BigDecimal amount, TransactionType type,
                        String description, LocalDate date) {
                return TransactionBaseDto.builder()
                                .user(user)
                                .category(category)
                                .amount(amount)
                                .type(type)
                                .description(description)
                                .date(date)
                                .build();
        }
}