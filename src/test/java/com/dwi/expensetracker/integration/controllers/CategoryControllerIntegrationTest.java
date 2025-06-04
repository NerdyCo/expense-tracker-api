package com.dwi.expensetracker.integration.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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
import com.dwi.expensetracker.domains.dtos.category.CategoryPatchDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestExecutionListeners(value = TransactionalTestExecutionListener.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class CategoryControllerIntegrationTest {
        private final MockMvc mockMvc;
        private final CategoryService categoryService;
        private final UserService userService;
        private final ObjectMapper objectMapper;

        @Autowired
        public CategoryControllerIntegrationTest(MockMvc mockMvc, CategoryService categoryService,
                        UserService userService, ObjectMapper objectMapper) {
                this.mockMvc = mockMvc;
                this.categoryService = categoryService;
                this.userService = userService;
                this.objectMapper = objectMapper;
        }

        private static final String BASE_URL = "/api/v1/categories";

        @Test
        @DisplayName("1. Should create category and return 201 CREATED")
        public void shouldCreateCategoryAndReturn201() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                CategoryRequestDto requestDto = TestDataUtil.givenCategoryDtoA(user.getId());

                String json = objectMapper.writeValueAsString(requestDto);

                mockMvc.perform(post(BASE_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").exists())
                                .andExpect(jsonPath("$.name").value("Food & Beverage"));
        }

        @Test
        @DisplayName("2. Should get category by ID and return 200 OK")
        public void shouldGetCategoryById() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));

                mockMvc.perform(get(BASE_URL + "/" + category.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(category.getId().toString()))
                                .andExpect(jsonPath("$.name").value("Food & Beverage"));
        }

        @Test
        @DisplayName("3. Should return 404 NOT FOUND for non-existent category")
        public void shouldReturn404WhenCategoryNotFound() throws Exception {
                mockMvc.perform(get(BASE_URL + "/" + UUID.randomUUID()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("4. Should partially update category and return 200 OK")
        public void shouldUpdateCategoryPartially() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));

                CategoryPatchDto patchDto = CategoryPatchDto.builder()
                                .name("Updated Name")
                                .build();

                String json = objectMapper.writeValueAsString(patchDto);

                mockMvc.perform(patch(BASE_URL + "/" + category.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name").value("Updated Name"));
        }

        @Test
        @DisplayName("5. Should delete category and return 204 NO CONTENT")
        public void shouldDeleteCategory() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());
                Category category = categoryService.create(TestDataUtil.givenCategoryA(user));

                mockMvc.perform(delete(BASE_URL + "/" + category.getId()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("6. Should return 404 when deleting non-existent category")
        public void shouldReturn404OnDeleteIfCategoryNotFound() throws Exception {
                mockMvc.perform(delete(BASE_URL + "/" + UUID.randomUUID()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("7. Should return list of all categories")
        public void shouldReturnAllCategories() throws Exception {
                User user = userService.create(TestDataUtil.givenUserA());

                categoryService.create(TestDataUtil.givenCategoryA(user));
                categoryService.create(TestDataUtil.givenCategoryB(user));
                categoryService.create(TestDataUtil.givenCategoryC(user));

                mockMvc.perform(get(BASE_URL + "?page=0&size=10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(3));
        }

}
