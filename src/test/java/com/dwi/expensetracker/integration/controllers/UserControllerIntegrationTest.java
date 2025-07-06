package com.dwi.expensetracker.integration.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.dwi.expensetracker.TestAuthUtil;
import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Transaction;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.TransactionRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import dasniko.testcontainers.keycloak.KeycloakContainer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for UserController")
public class UserControllerIntegrationTest {
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
                // set keycloak container for TestAuthUtil
                TestAuthUtil.setKeycloakContainer(keycloak);

                // clear database
                userRepository.deleteAll();

                // create test user
                testUser = TestDataUtil.givenUserA();
                userRepository.save(testUser);

                testCategory = TestDataUtil.givenCategoryA(testUser);
                testCategory = categoryRepository.save(testCategory);

                testTransaction = TestDataUtil.givenTransactionA(testUser, testCategory);
                testTransaction = transactionRepository.save(testTransaction);

                // obtain token
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
        @DisplayName("1. get all user with admin role should return 200")
        public void getAllUsersWithAdminRoleShouldReturn200() throws Exception {
                mockMvc.perform(get("/api/v1/users")
                                .header("Authorization", "Bearer " + adminToken)
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content[0].id").value(testUser.getId()))
                                .andExpect(jsonPath("$.content[0].email").value(testUser.getEmail()))
                                .andExpect(jsonPath("$.content[0].username").value(testUser.getUsername()))
                                .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("2. Get All Users With UserRole Should Return 403")
        void GetAllUsersWithUserRoleShouldReturn403() throws Exception {
                mockMvc.perform(get("/api/v1/users")
                                .header("Authorization", "Bearer " + userToken)
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Get All Users Without Authentication Should Return 401")
        void GetAllUsersWithoutAuthenticationShouldReturn401() throws Exception {
                mockMvc.perform(get("/api/v1/users")
                                .param("page", "0")
                                .param("size", "10"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("3. Get User By Id With AdminRole Should Return 200")
        void GetUserByIdWithAdminRoleShouldReturn200() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId())
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testUser.getId()))
                                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                                .andExpect(jsonPath("$.username").value(testUser.getUsername()));
        }

        @Test
        @DisplayName("4. Get User By Id With UserRole Should Return 403")
        void GetUserByIdWithUserRoleShouldReturn403() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId())
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("5. Get User By Id Without Authentication Should Return 401")
        void GetUserByIdWithoutAuthenticationShouldReturn401() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId()))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("6. Get User By Id With Invalid Id Should Return 404")
        void GetUserByIdWithInvalidIdShouldReturn404() throws Exception {
                mockMvc.perform(get("/api/v1/users/invalid-id")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("7. Get User Categories With UserRole Should Return 200")
        void GetUserCategoriesWithUserRoleShouldReturn200() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId() + "/categories")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value(testCategory.getName()))
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("8. Get User Categories With AdminRole Should Return 200")
        void GetUserCategoriesWithAdminRoleShouldReturn200() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId() + "/categories")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].name").value(testCategory.getName()))
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("9. Get User Categories Without Authentication Should Return 401")
        void GetUserCategoriesWithoutAuthenticationShouldReturn401() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId() + "/categories"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("10. Get User Categories With Invalid UserId Should Return 404")
        void GetUserCategoriesWithInvalidUserIdShouldReturn404() throws Exception {
                mockMvc.perform(get("/api/v1/users/invalid-id/categories")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("11. Get User Transactions With UserRole Should Return 200")
        void GetUserTransactionsWithUserRoleShouldReturn200() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId() + "/transactions")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].amount").value(testTransaction.getAmount().toString()))
                                .andExpect(jsonPath("$[0].description").value(testTransaction.getDescription()))
                                .andExpect(jsonPath("$[0].type").value(testTransaction.getType().toString()))
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("12. Get User Transactions With AdminRole Should Return 200")
        void GetUserTransactionsWithAdminRoleShouldReturn200() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId() + "/transactions")
                                .header("Authorization", "Bearer " + adminToken))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].amount").value(testTransaction.getAmount().toString()))
                                .andExpect(jsonPath("$[0].description").value(testTransaction.getDescription()))
                                .andExpect(jsonPath("$[0].type").value(testTransaction.getType().toString()))
                                .andExpect(jsonPath("$.length()").value(1));
        }

        @Test
        @DisplayName("13. Get User Transactions Without Authentication Should Return 401")
        void GetUserTransactionsWithoutAuthenticationShouldReturn401() throws Exception {
                mockMvc.perform(get("/api/v1/users/" + testUser.getId() + "/transactions"))
                                .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("14. Get User Transactions With Invalid UserId Should Return 404")
        void GetUserTransactionsWithInvalidUserIdShouldReturn404() throws Exception {
                mockMvc.perform(get("/api/v1/users/invalid-id/transactions")
                                .header("Authorization", "Bearer " + userToken))
                                .andExpect(status().isNotFound());
        }
}
