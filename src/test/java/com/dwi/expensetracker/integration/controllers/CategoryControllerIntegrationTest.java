package com.dwi.expensetracker.integration.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.dtos.category.CreateCategoryDto;
import com.dwi.expensetracker.domains.dtos.customer.CreateCustomerDto;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.services.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final CategoryService categoryService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CategoryControllerIntegrationTest(
            MockMvc mockMvc,
            CategoryService categoryService,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.categoryService = categoryService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateCategoryReturns201() throws Exception {
        CreateCustomerDto customerDto = TestDataUtil.createTestCustomerDtoA();
        CreateCategoryDto categoryDto = TestDataUtil.createTestCategoryDtoA(customerDto);
        String categoryJson = objectMapper.writeValueAsString(categoryDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateCategoryReturnsSavedCategory() throws Exception {
        CreateCustomerDto customerDto = TestDataUtil.createTestCustomerDtoA();
        CreateCategoryDto categoryDto = TestDataUtil.createTestCategoryDtoA(customerDto);
        String categoryJson = objectMapper.writeValueAsString(categoryDto);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Food & Beverage"))
                .andExpect(jsonPath("$.customer").exists())
                .andExpect(jsonPath("$.customer.id").exists())
                .andExpect(jsonPath("$.customer.username").value(customerDto.getUsername()));
    }

    @Test
    public void testGetCategoryReturns200WhenExists() throws Exception {
        CustomerEntity customerEntity = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity categoryEntity = categoryService.save(TestDataUtil.createTestCategoryEntityA(customerEntity));

        mockMvc.perform(get("/api/categories/" + categoryEntity.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatCategoryReturnsCorrectData() throws Exception {
        CustomerEntity customerEntity = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity categoryEntity = categoryService.save(TestDataUtil.createTestCategoryEntityA(customerEntity));

        mockMvc.perform(get("/api/categories/" + categoryEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Food & Beverage"))
                .andExpect(jsonPath("$.customer").exists())
                .andExpect(jsonPath("$.customer.id").exists())
                .andExpect(jsonPath("$.customer.username").value(customerEntity.getUsername()));
    }

    @Test
    public void testGetCategoryReturns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFullUpdateReturns200AndUpdatedData() throws Exception {
        CustomerEntity customerEntity = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity savedCategoryEntity = categoryService
                .save(TestDataUtil.createTestCategoryEntityA(customerEntity));
        CreateCategoryDto updateCategory = TestDataUtil.createTestCategoryDtoA(null);
        updateCategory.setName("UPDATED");
        String categoryJson = objectMapper.writeValueAsString(updateCategory);

        mockMvc.perform(patch("/api/categories/" + savedCategoryEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATED"));
    }

    @Test
    public void testFullUpdateReturns404WhenNotExists() throws Exception {
        CreateCustomerDto customerDto = TestDataUtil.createTestCustomerDtoA();
        CreateCategoryDto categoryDto = TestDataUtil.createTestCategoryDtoA(customerDto);
        String categoryJson = objectMapper.writeValueAsString(categoryDto);

        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPartialUpdateReturns200AndUpdatedField() throws Exception {
        CustomerEntity customerEntity = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity savedCategoryEntity = categoryService
                .save(TestDataUtil.createTestCategoryEntityA(customerEntity));
        CreateCategoryDto updateCategory = TestDataUtil.createTestCategoryDtoA(null);
        updateCategory.setName("UPDATED");
        String categoryJson = objectMapper.writeValueAsString(updateCategory);

        mockMvc.perform(patch("/api/categories/" + savedCategoryEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATED"));
    }

    @Test
    public void testDeleteCategoryReturns204() throws Exception {
        CustomerEntity customerEntity = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity categoryEntity = categoryService.save(TestDataUtil.createTestCategoryEntityA(customerEntity));

        mockMvc.perform(delete("/api/categories/" + categoryEntity.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCategoryReturns404WhenNotExist() throws Exception {
        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound());
    }
}
