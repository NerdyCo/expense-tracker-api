package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for CategoryRepository")
public class CategoryRepositoryIntegrationTest {

    private final CategoryRepository underTest;
    private final UserRepository userRepository;

    @Autowired
    public CategoryRepositoryIntegrationTest(CategoryRepository underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    @DisplayName("1. Should save and retrieve category by ID")
    public void shouldSaveAndRetrieveCategoryById() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category category = TestDataUtil.givenCategoryA(user);

        underTest.saveAndFlush(category);
        Optional<Category> result = underTest.findById(category.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Food & Beverage");
        assertThat(result.get().getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("2. Should save multiple categories and retrieve all")
    public void shouldSaveMultipleCategoriesAndRetrieveAll() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());

        Category categoryA = TestDataUtil.givenCategoryA(user);
        Category categoryB = TestDataUtil.givenCategoryB(user);
        Category categoryC = TestDataUtil.givenCategoryC(user);

        underTest.saveAllAndFlush(List.of(categoryA, categoryB, categoryC));
        List<Category> result = underTest.findAll();

        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(categoryA, categoryB, categoryC);
    }

    @Test
    @DisplayName("3. Should check if category exists by user ID and name")
    public void shouldCheckIfCategoryExistsByUserIdAndName() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category category = TestDataUtil.givenCategoryA(user);
        underTest.saveAndFlush(category);

        boolean exists = underTest.existsByUserIdAndName(user.getId(), "Food & Beverage");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("4. Should return false if category name does not exist for user")
    public void shouldReturnFalseIfCategoryDoesNotExistByUserIdAndName() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());

        boolean exists = underTest.existsByUserIdAndName(user.getId(), "Nonexistent");

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("5. Should retrieve category with user using findByIdWithUser")
    public void shouldRetrieveCategoryWithUserUsingFindByIdWithUser() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category category = TestDataUtil.givenCategoryA(user);

        underTest.saveAndFlush(category);
        Optional<Category> result = underTest.findByIdWithUser(category.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUser()).isEqualTo(user);
        assertThat(result.get().getName()).isEqualTo("Food & Beverage");
    }

    @Test
    @DisplayName("6. Should find categories by user ID")
    public void shouldFindCategoriesByUserId() {
        User user = userRepository.saveAndFlush(TestDataUtil.givenUserA());
        Category categoryA = TestDataUtil.givenCategoryA(user);
        Category categoryB = TestDataUtil.givenCategoryB(user);

        underTest.saveAllAndFlush(List.of(categoryA, categoryB));
        List<Category> result = underTest.findByUserId(user.getId());

        assertThat(result)
                .hasSize(2)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Food & Beverage", "Transportation");
    }

    @Test
    @DisplayName("7. Should return empty when category not found by ID with user")
    public void shouldReturnEmptyWhenCategoryNotFoundByIdWithUser() {
        Optional<Category> result = underTest.findByIdWithUser(UUID.randomUUID());

        assertThat(result).isNotPresent();
    }
}
