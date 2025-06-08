package com.dwi.expensetracker.integration.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;

import com.dwi.expensetracker.TestAuthUtil;
import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionPatchDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.enums.TransactionType;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.TransactionService;
import com.dwi.expensetracker.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TransactionControllerIntegrationTest {

        private final MockMvc mockMvc;
        private final UserService userService;
        private final CategoryService categoryService;
        private final TransactionService transactionService;
        private final ObjectMapper objectMapper;

        @Autowired
        public TransactionControllerIntegrationTest(MockMvc mockMvc, UserService userService,
                        CategoryService categoryService, TransactionService transactionService,
                        ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.userService = userService;
                this.categoryService = categoryService;
                this.transactionService = transactionService;
                this.objectMapper = objectMapper;
        }

        private static final String BASE_URL = "/api/v1/transactions";
        private String jwtToken;

        @BeforeEach
        void setUpJwtToken() throws Exception {
                jwtToken = TestAuthUtil.obtainJwtToken(mockMvc, objectMapper);
        }

        @Test
        @DisplayName("1. Should create transaction and return 201 CREATED")
        public void shouldCreateTransaction() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                TransactionRequestDto requestDto = TransactionRequestDto.builder()
                                .userId(user.getId())
                                .categoryId(category.getId())
                                .amount(new BigDecimal("30000"))
                                .type(TransactionType.EXPENSE)
                                .description("Lunch with friends")
                                .date(LocalDate.of(2025, 5, 1))
                                .build();

                String json = objectMapper.writeValueAsString(requestDto);

                mockMvc.perform(post(BASE_URL)
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.amount").value(30000))
                                .andExpect(jsonPath("$.type").value("EXPENSE"))
                                .andExpect(jsonPath("$.description").value("Lunch with friends"));
        }

        @Test
        @DisplayName("2. Should get transaction by ID and return 200 OK")
        public void shouldGetTransactionById() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                Transaction transaction = transactionService.create(TestDataUtil.givenTransactionA(user, category));

                mockMvc.perform(get(BASE_URL + "/" + transaction.getId())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(transaction.getId().toString()))
                                .andExpect(jsonPath("$.amount").value(30000));
        }

        @Test
        @DisplayName("3. Should return 404 when transaction not found")
        public void shouldReturn404WhenTransactionNotFound() throws Exception {
                mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("4. Should return return paginated transactions list")
        public void shouldReturnAllTransactions() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));

                transactionService.create(TestDataUtil.givenTransactionA(user, category));
                transactionService.create(TestDataUtil.givenTransactionB(user, category));
                transactionService.create(TestDataUtil.givenTransactionC(user, category));

                mockMvc.perform(get(BASE_URL + "?page=0&size=10")
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(3));
        }

        @Test
        @DisplayName("5. Should update transaction partially and return 200 OK")
        public void shouldUpdateTransactionPartially() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                Transaction transaction = transactionService.create(TestDataUtil.givenTransactionA(user, category));

                TransactionPatchDto patchDto = TransactionPatchDto.builder()
                                .amount(new BigDecimal("50000"))
                                .description("Updated Description")
                                .build();

                String json = objectMapper.writeValueAsString(patchDto);

                mockMvc.perform(patch(BASE_URL + "/" + transaction.getId())
                                .header("Authorization", "Bearer " + jwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.amount").value(50000))
                                .andExpect(jsonPath("$.description").value("Updated Description"));
        }

        @Test
        @DisplayName("6. Should delete transaction and return 204 NO CONTENT")
        public void shouldDeleteTransaction() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));
                Transaction transaction = transactionService.create(TestDataUtil.givenTransactionA(user, category));

                mockMvc.perform(delete(BASE_URL + "/" + transaction.getId())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("7. Should return 404 on deleting non-existent transaction")
        public void shouldReturn404OnDeleteTransactionNotFound() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID())
                                .header("Authorization", "Bearer " + jwtToken))
                                .andExpect(status().isNotFound());
        }
}
