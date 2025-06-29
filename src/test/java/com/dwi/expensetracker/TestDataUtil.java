package com.dwi.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.dwi.expensetracker.domains.dtos.auth.RegisterUserDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.enums.TransactionType;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestDataUtil {
        // === Constants ===
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

        public static final String USER_ID_A = UUID.randomUUID().toString();
        public static final String USER_ID_B = UUID.randomUUID().toString();
        public static final String USER_ID_C = UUID.randomUUID().toString();

        // === USER ===
        public static User givenUserA() {
                return createUser(USER_ID_A, EMAIL_A, USERNAME_A);
        }

        public static User givenUserB() {
                return createUser(USER_ID_B, EMAIL_B, USERNAME_B);
        }

        public static User givenUserC() {
                return createUser(USER_ID_C, EMAIL_C, USERNAME_C);
        }

        public static RegisterUserDto givenRegisterUserDtoA() {
                return createRegisterUserDto(EMAIL_A, USERNAME_A, PASSWORD);
        }

        public static RegisterUserDto givenRegisterUserDtoB() {
                return createRegisterUserDto(EMAIL_B, USERNAME_B, PASSWORD);
        }

        public static RegisterUserDto givenRegisterUserDtoC() {
                return createRegisterUserDto(EMAIL_C, USERNAME_C, PASSWORD);
        }

        private static User createUser(String id, String email, String username) {
                return User.builder()
                                .id(id)
                                .email(email)
                                .username(username)
                                .build();
        }

        private static RegisterUserDto createRegisterUserDto(String email, String username, String password) {
                return RegisterUserDto.builder()
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

        public static CategoryRequestDto givenCategoryDtoA(String userId) {
                return createCategoryDto(userId, "Food & Beverage");
        }

        public static CategoryRequestDto givenCategoryDtoB(String userId) {
                return createCategoryDto(userId, "Transportation");
        }

        public static CategoryRequestDto givenCategoryDtoC(String userId) {
                return createCategoryDto(userId, "Hobby");
        }

        private static Category createCategory(User user, String name) {
                return Category.builder()
                                .user(user)
                                .name(name)
                                .build();
        }

        private static CategoryRequestDto createCategoryDto(String userId, String name) {
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

        public static TransactionRequestDto givenTransactionRequestDtoA(String userId, UUID categoryId) {
                return createTransactionRequestDto(userId, categoryId, AMOUNT_A, TransactionType.EXPENSE,
                                "Lunch with friends", DATE_A);
        }

        public static TransactionRequestDto givenTransactionRequestDtoB(String userId, UUID categoryId) {
                return createTransactionRequestDto(userId, categoryId, AMOUNT_B, TransactionType.INCOME,
                                "Freelance project", DATE_B);
        }

        public static TransactionRequestDto givenTransactionRequestDtoC(String userId, UUID categoryId) {
                return createTransactionRequestDto(userId, categoryId, AMOUNT_C, TransactionType.EXPENSE, "Coffee",
                                DATE_C);
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

        private static TransactionRequestDto createTransactionRequestDto(String userId, UUID categoryId,
                        BigDecimal amount, TransactionType type, String description, LocalDate date) {
                return TransactionRequestDto.builder()
                                .userId(userId)
                                .categoryId(categoryId)
                                .amount(amount)
                                .type(type)
                                .description(description)
                                .date(date)
                                .build();
        }
}