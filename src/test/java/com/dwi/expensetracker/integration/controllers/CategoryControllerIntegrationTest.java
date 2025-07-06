package com.dwi.expensetracker.integration.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.dwi.expensetracker.domains.dtos.category.CategoryPatchDto;
import com.dwi.expensetracker.domains.dtos.category.CategoryRequestDto;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import dasniko.testcontainers.keycloak.KeycloakContainer;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for CategoryController")
public class CategoryControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Container
    private static final KeycloakContainer keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:26.0.2")
            .withRealmImportFile("expense-realm.json")
            .withAdminUsername("admin")
            .withAdminPassword("admin");

    private String adminToken;
    private String userToken;
    private User testUser;
    private Category testCategory;

    @DynamicPropertySource
    static void registerKeycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloak.getAuthServerUrl() + "/realms/expense-realm");
        registry.add("keycloak.server-url", keycloak::getAuthServerUrl);
    }

    @BeforeEach
    void setUp() throws Exception {
        TestAuthUtil.setKeycloakContainer(keycloak);
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        testUser = TestDataUtil.givenUserA();
        userRepository.save(testUser);

        testCategory = TestDataUtil.givenCategoryA(testUser);
        testCategory = categoryRepository.save(testCategory);

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
    @DisplayName("1. Create Category With User Role Should Return 201")
    void createCategoryWithUserRoleShouldReturn201() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New Category");
        requestDto.setUserId(testUser.getId());

        mockMvc.perform(post("/api/v1/categories	union")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Category"))
                .andExpect(jsonPath("$.userId").value(testUser.getId()));
    }

    @Test
    @DisplayName("2. Create Category With Wrong UserId Should Return 400")
    void createCategoryWithWrongUserIdShouldReturn400() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New Category");
        requestDto.setUserId("wrong-user-id");

        mockMvc.perform(post("/api/v1/categories")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("3. Create Category Without Authentication Should Return 401")
    void createCategoryWithoutAuthenticationShouldReturn401() throws Exception {
        CategoryRequestDto requestDto = new CategoryRequestDto();
        requestDto.setName("New Category");
        requestDto.setUserId(testUser.getId());

        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("4. Get All Categories With Admin Role Should Return 200")
    void getAllCategoriesWithAdminRoleShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/categories")
                .header("Authorization", "Bearer " + adminToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value(testCategory.getName()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("5. Get All Categories With User Role Should Return 403")
    void getAllCategoriesWithUserRoleShouldReturn403() throws Exception {
        mockMvc.perform(get("/api/v1/categories")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("6. Get All Categories Without Authentication Should Return 401")
    void getAllCategoriesWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/categories")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("7. Get Category By Id With User Role Should Return 200")
    void getCategoryByIdWithUserRoleShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/categories/" + testCategory.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId().toString()))
                .andExpect(jsonPath("$.name").value(testCategory.getName()));
    }

    @Test
    @DisplayName("8. Get Category By Id With Admin Role Should Return 200")
    void getCategoryByIdWithAdminRoleShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/categories/" + testCategory.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testCategory.getId().toString()))
                .andExpect(jsonPath("$.name").value(testCategory.getName()));
    }

    @Test
    @DisplayName("9. Get Category By Id Without Authentication Should Return 401")
    void getCategoryByIdWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/categories/" + testCategory.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("10. Get Category By Id With Invalid Id Should Return 404")
    void getCategoryByIdWithInvalidIdShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/categories/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("11. Update Category Partially With User Role Should Return 200")
    void updateCategoryPartiallyWithUserRoleShouldReturn200() throws Exception {
        CategoryPatchDto patchDto = new CategoryPatchDto();
        patchDto.setName("Updated Category");

        mockMvc.perform(patch("/api/v1/categories/" + testCategory.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    @DisplayName("12. Update Category Partially With Wrong User Should Return 400")
    void updateCategoryPartiallyWithWrongUserShouldReturn400() throws Exception {
        CategoryPatchDto patchDto = new CategoryPatchDto();
        patchDto.setName("Updated Category");

        // Create a category with a different user
        User otherUser = TestDataUtil.givenUserA();
        otherUser.setId("different-user-id");
        userRepository.save(otherUser);
        Category otherCategory = TestDataUtil.givenCategoryA(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        mockMvc.perform(patch("/api/v1/categories/" + otherCategory.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("13. Update Category Partially Without Authentication Should Return 401")
    void updateCategoryPartiallyWithoutAuthenticationShouldReturn401() throws Exception {
        CategoryPatchDto patchDto = new CategoryPatchDto();
        patchDto.setName("Updated Category");

        mockMvc.perform(patch("/api/v1/categories/" + testCategory.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("14. Delete Category With User Role Should Return 204")
    void deleteCategoryWithUserRoleShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/" + testCategory.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("15. Delete Category With Wrong User Should Return 400")
    void deleteCategoryWithWrongUserShouldReturn400() throws Exception {
        User otherUser = TestDataUtil.givenUserA();
        otherUser.setId("different-user-id");
        userRepository.save(otherUser);
        Category otherCategory = TestDataUtil.givenCategoryA(otherUser);
        otherCategory = categoryRepository.save(otherCategory);

        mockMvc.perform(delete("/api/v1/categories/" + otherCategory.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("16. Delete Category Without Authentication Should Return 401")
    void deleteCategoryWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/" + testCategory.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("17. Delete Category With Invalid Id Should Return 404")
    void deleteCategoryWithInvalidIdShouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/v1/categories/" + UUID.randomUUID())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
