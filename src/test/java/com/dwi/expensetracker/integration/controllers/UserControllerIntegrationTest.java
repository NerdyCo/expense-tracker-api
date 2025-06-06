package com.dwi.expensetracker.integration.controllers;

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

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.dtos.user.UserPatchDto;
import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.TransactionService;
import com.dwi.expensetracker.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserControllerIntegrationTest(
            MockMvc mockMvc,
            UserService userService,
            CategoryService categoryService,
            TransactionService transactionService,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.categoryService = categoryService;
        this.transactionService = transactionService;
        this.objectMapper = objectMapper;
    }

    private static final String BASE_URL = "/api/v1/users";

    @Test
    @DisplayName("1. Should return create a user and return 201 CREATED")
    public void shouldCreateUserAndReturn201() throws Exception {
        UserRequestDto requestDto = TestDataUtil.givenUserRequestDtoA();
        String json = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("kautsar"))
                .andExpect(jsonPath("$.email").value("kautsar@gmail.com"));
    }

    @Test
    @DisplayName("2. Should return 200 OK and correct user data when user exists")
    public void shouldReturn200AndUserDataIfExists() throws Exception {
        User savedUser = userService.create(TestDataUtil.givenUserA());

        mockMvc.perform(get(BASE_URL + "/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.username").value("kautsar"))
                .andExpect(jsonPath("$.email").value("kautsar@gmail.com"));
    }

    @Test
    @DisplayName("3. Should return 404 NOT FOUND when user does not exist")
    public void shouldReturn404WhenUserNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get(BASE_URL + "/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("4. Should return 200 OK and updated field on partial update")
    public void shouldUpdateUserPartially() throws Exception {
        User savedUser = userService.create(TestDataUtil.givenUserA());
        UserPatchDto patchDto = UserPatchDto.builder().username("UPDATED").build();
        String json = objectMapper.writeValueAsString(patchDto);

        mockMvc.perform(patch(BASE_URL + "/" + savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("UPDATED"));
    }

    @Test
    @DisplayName("5. Should delete user and return 204 NO CONTENT")
    public void shouldDeleteUser() throws Exception {
        User savedUser = userService.create(TestDataUtil.givenUserA());

        mockMvc.perform(delete(BASE_URL + "/" + savedUser.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("6. Should return 404 on delete if user does not exist")
    public void shouldReturn404OnDeleteIfNotExist() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("7. Should return 200 OK and list all users")
    public void shouldReturnAllUsers() throws Exception {
        userService.create(TestDataUtil.givenUserA());
        userService.create(TestDataUtil.givenUserB());
        userService.create(TestDataUtil.givenUserC());

        mockMvc.perform(get(BASE_URL + "?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    @DisplayName("8. Should return 200 OK and user's categories")
    public void shouldReturnUserCategories() throws Exception {
        User user = userService.create(TestDataUtil.givenUserA());

        categoryService.create(TestDataUtil.givenCategoryA(user));
        categoryService.create(TestDataUtil.givenCategoryB(user));

        mockMvc.perform(get(BASE_URL + "/" + user.getId() + "/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("9. Should return 200 OK and user's transactions")
    public void shouldReturnUserTransactions() throws Exception {
        User user = userService.create(TestDataUtil.givenUserA());
        Category category = categoryService.create(TestDataUtil.givenCategoryA(user));

        transactionService.create(TestDataUtil.givenTransactionA(user, category));
        transactionService.create(TestDataUtil.givenTransactionB(user, category));

        mockMvc.perform(get(BASE_URL + "/" + user.getId() + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

}
