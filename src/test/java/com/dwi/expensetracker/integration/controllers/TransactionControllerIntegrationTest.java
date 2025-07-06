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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.dwi.expensetracker.TestAuthUtil;
import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionPatchDto;
import com.dwi.expensetracker.domains.dtos.transaction.TransactionRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.domains.enums.TransactionType;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.TransactionRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import dasniko.testcontainers.keycloak.KeycloakContainer;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for TransactionController")
public class TransactionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.0.2")
            .withRealmImportFile("expense-realm.json")
            .withAdminUsername("admin")
            .withAdminPassword("admin");

    private String adminToken;
    private String userToken;
    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;

    @DynamicPropertySource
    static void registerKeycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/expense-realm");
        registry.add("keycloak.server-url", keycloak::getAuthServerUrl);
    }

    @BeforeEach
    void setUp() throws Exception {
        TestAuthUtil.setKeycloakContainer(keycloak);
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = TestDataUtil.givenUserA();
        userRepository.save(testUser);

        testCategory = TestDataUtil.givenCategoryA(testUser);
        testCategory = categoryRepository.save(testCategory);

        testTransaction = TestDataUtil.givenTransactionA(testUser, testCategory);
        testTransaction = transactionRepository.save(testTransaction);

        adminToken = TestAuthUtil.obtainJwtToken(
                mockMvc,
                objectMapper,
                "kautsar",
                "Kautsar123!",
                "expense-tracker-admin",
                "UBtQnrk3q3vTeoYzIH9ozrn4URpHnoph");
        userToken = TestAuthUtil.obtainJwtToken(
                mockMvc,
                objectMapper,
                "kautsar",
                "Kautsar123!",
                "expense-tracker-admin",
                "UBtQnrk3q3vTeoYzIH9ozrn4URpHnoph");
    }

    @Test
    @DisplayName("1. Create Transaction With User Role Should Return 201")
    void createTransactionWithUserRoleShouldReturn201() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setUserId(testUser.getId());
        requestDto.setCategoryId(testCategory.getId());
        requestDto.setAmount(new BigDecimal(100.0));
        requestDto.setDescription("Test Transaction");
        requestDto.setType(TransactionType.EXPENSE);
        requestDto.setDate(LocalDate.parse("2025-07-06"));

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.description").value("Test Transaction"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    @DisplayName("2. Create Transaction With Wrong UserId Should Return 400")
    void createTransactionWithWrongUserIdShouldReturn400() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setUserId("wrong-user-id");
        requestDto.setCategoryId(testCategory.getId());
        requestDto.setAmount(new BigDecimal(100.0));
        requestDto.setDescription("Test Transaction");
        requestDto.setType(TransactionType.EXPENSE);
        requestDto.setDate(LocalDate.parse("2025-07-06"));

        mockMvc.perform(post("/api/v1/transactions")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("3. Create Transaction Without Authentication Should Return 401")
    void createTransactionWithoutAuthenticationShouldReturn401() throws Exception {
        TransactionRequestDto requestDto = new TransactionRequestDto();
        requestDto.setUserId(testUser.getId());
        requestDto.setCategoryId(testCategory.getId());
        requestDto.setAmount(new BigDecimal(100.0));
        requestDto.setDescription("Test Transaction");
        requestDto.setType(TransactionType.EXPENSE);
        requestDto.setDate(LocalDate.parse("2025-07-06"));

        mockMvc.perform(post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("4. Get All Transactions With Admin Role Should Return 200")
    void getAllTransactionsWithAdminRoleShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].amount").value(testTransaction.getAmount().toString()))
                .andExpect(jsonPath("$.content[0].description").value(testTransaction.getDescription()))
                .andExpect(jsonPath("$.content[0].type").value(testTransaction.getType().toString()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("5. Get All Transactions With User Role Should Return 403")
    void getAllTransactionsWithUserRoleShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("6. Get All Transactions Without Authentication Should Return 401")
    void getAllTransactionsWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/transactions")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("7. Get Transaction By Id With User Role Should Return 200")
    void getTransactionByIdWithUserRoleShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/" + testTransaction.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTransaction.getId().toString()))
                .andExpect(jsonPath("$.amount").value(testTransaction.getAmount().toString()))
                .andExpect(jsonPath("$.description").value(testTransaction.getDescription()))
                .andExpect(jsonPath("$.type").value(testTransaction.getType().toString()));
    }

    @Test
    @DisplayName("8. Get Transaction By Id With Admin Role Should Return 200")
    void getTransactionByIdWithAdminRoleShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/" + testTransaction.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTransaction.getId().toString()))
                .andExpect(jsonPath("$.amount").value(testTransaction.getAmount().toString()))
                .andExpect(jsonPath("$.description").value(testTransaction.getDescription()))
                .andExpect(jsonPath("$.type").value(testTransaction.getType().toString()));
    }

    @Test
    @DisplayName("9. Get Transaction By Id Without Authentication Should Return 401")
    void getTransactionByIdWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/" + testTransaction.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("10. Get Transaction By Id With Invalid Id Should Return 404")
    void getTransactionByIdWithInvalidIdShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/transactions/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("11. Update Transaction Partially With User Role Should Return 200")
    void updateTransactionPartiallyWithUserRoleShouldReturn200() throws Exception {
        TransactionPatchDto patchDto = new TransactionPatchDto();
        patchDto.setAmount(new BigDecimal(200.0));
        patchDto.setDescription("Updated Transaction");
        patchDto.setType(TransactionType.INCOME);

        mockMvc.perform(patch("/api/v1/transactions/" + testTransaction.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.description").value("Updated Transaction"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    @DisplayName("12. Update Transaction Partially With Wrong User Should Return 400")
    void updateTransactionPartiallyWithWrongUserShouldReturn400() throws Exception {
        User otherUser = TestDataUtil.givenUserA();
        otherUser.setId("different-user-id");
        userRepository.save(otherUser);
        Transaction otherTransaction = TestDataUtil.givenTransactionA(otherUser, testCategory);
        otherTransaction = transactionRepository.save(otherTransaction);

        TransactionPatchDto patchDto = new TransactionPatchDto();
        patchDto.setAmount(new BigDecimal(200.0));
        patchDto.setDescription("Updated Transaction");
        patchDto.setType(TransactionType.INCOME);

        mockMvc.perform(patch("/api/v1/transactions/" + otherTransaction.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("13. Update Transaction Partially Without Authentication Should Return 401")
    void updateTransactionPartiallyWithoutAuthenticationShouldReturn401() throws Exception {
        TransactionPatchDto patchDto = new TransactionPatchDto();
        patchDto.setAmount(new BigDecimal(200.0));
        patchDto.setDescription("Updated Transaction");
        patchDto.setType(TransactionType.INCOME);

        mockMvc.perform(patch("/api/v1/transactions/" + testTransaction.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("14. Delete Transaction With User Role Should Return 204")
    void deleteTransactionWithUserRoleShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/transactions/" + testTransaction.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("15. Delete Transaction With Wrong User Should Return 400")
    void deleteTransactionWithWrongUserShouldReturn400() throws Exception {
        User otherUser = TestDataUtil.givenUserA();
        otherUser.setId("different-user-id");
        userRepository.save(otherUser);
        Transaction otherTransaction = TestDataUtil.givenTransactionA(otherUser, testCategory);
        otherTransaction = transactionRepository.save(otherTransaction);

        mockMvc.perform(delete("/api/v1/transactions/" + otherTransaction.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("16. Delete Transaction Without Authentication Should Return 401")
    void deleteTransactionWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/v1/transactions/" + testTransaction.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("17. Delete Transaction With Invalid Id Should Return 404")
    void deleteTransactionWithInvalidIdShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/v1/transactions/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
