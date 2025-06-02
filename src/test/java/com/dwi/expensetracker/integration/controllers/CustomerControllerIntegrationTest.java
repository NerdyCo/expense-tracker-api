package com.dwi.expensetracker.integration.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.dtos.user.UserRequestDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerControllerIntegrationTest {
    private final MockMvc mockMvc;
    private final UserService customerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CustomerControllerIntegrationTest(
            MockMvc mockMvc,
            UserService customerService,
            ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.customerService = customerService;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateCustomerReturns201() throws Exception {
        UserRequestDto customerDto = TestDataUtil.createTestCustomerDtoA();
        String customerJson = objectMapper.writeValueAsString(customerDto);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateCustomerReturnsSavedCustomer() throws Exception {
        UserRequestDto customerDto = TestDataUtil.createTestCustomerDtoA();
        String customerJson = objectMapper.writeValueAsString(customerDto);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("kautsar"))
                .andExpect(jsonPath("$.email").value("kautsar@gmail.com"));
    }

    @Test
    public void testGetCustomerReturns200WhenExists() throws Exception {
        User customerEntity = customerService.save(TestDataUtil.createTestCustomerEntityA());
        mockMvc.perform(get("/api/customers/" + customerEntity.getId()))
                .andExpect(status().isOk());
    }

    @Test
    public void testThatCustomerReturnsCorrectData() throws Exception {
        User customerEntity = customerService.save(TestDataUtil.createTestCustomerEntityA());

        mockMvc.perform(get("/api/customers/" + customerEntity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("kautsar"))
                .andExpect(jsonPath("$.email").value("kautsar@gmail.com"));
    }

    @Test
    public void testGetCustomerReturns404WhenNotFound() throws Exception {
        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFullUpdateReturns200AndUpdatedData() throws Exception {
        User savedCustomerEntity = customerService.save(TestDataUtil.createTestCustomerEntityA());
        UserRequestDto updateCustomerDto = TestDataUtil.createTestCustomerDtoB();
        String customerJson = objectMapper.writeValueAsString(updateCustomerDto);

        mockMvc.perform(put("/api/customers/" + savedCustomerEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("teguh"))
                .andExpect(jsonPath("$.email").value("teguh@gmail.com"));
    }

    @Test
    public void testFullUpdateReturns404WhenNotExists() throws Exception {
        UserRequestDto customerDto = TestDataUtil.createTestCustomerDtoA();
        String customerJson = objectMapper.writeValueAsString(customerDto);

        mockMvc.perform(put("/api/customers/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testPartialUpdateReturns200AndUpdatedField() throws Exception {
        User savedCustomerEntity = customerService.save(TestDataUtil.createTestCustomerEntityA());
        UserRequestDto updateCustomer = TestDataUtil.createTestCustomerDtoA();
        updateCustomer.setUsername("UPDATED");
        String customerJson = objectMapper.writeValueAsString(updateCustomer);

        mockMvc.perform(patch("/api/customers/" + savedCustomerEntity.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("UPDATED"));
    }

    @Test
    public void testDeleteCustomerReturns204() throws Exception {
        User savedCustomerEntity = customerService.save(TestDataUtil.createTestCustomerEntityA());

        mockMvc.perform(delete("/api/customers/" + savedCustomerEntity.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteCustomerReturns404WhenNotExist() throws Exception {
        mockMvc.perform(delete("/api/customers/999"))
                .andExpect(status().isNotFound());
    }
}
