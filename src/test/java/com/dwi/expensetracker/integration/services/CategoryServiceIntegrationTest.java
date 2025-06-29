package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.CategoryService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for CategoryServiceImpl")
public class CategoryServiceIntegrationTest {
    private final CategoryService underTest;
    private final UserRepository userRepository;

    @Autowired
    public CategoryServiceIntegrationTest(CategoryService underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    @DisplayName("1. Should create a category and retrieve it successfully")
    public void shouldCreateAndRetrieveCategory() {
        User user = TestDataUtil.givenUserA();
        userRepository.save(user);
        Category category = TestDataUtil.givenCategoryA(user);

        Category savedCategory = underTest.create(category);
        Category foundCategory = underTest.getById(savedCategory.getId());

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getName()).isEqualTo("Food & Beverage");
        assertThat(foundCategory.getUser().getUsername()).isEqualTo("kautsar");
    }

    @Test
    @DisplayName("2. Should create multiple categories and retrieve all")
    public void shouldCreateMultipleCategoriesAndRetrieveAll() {
        User userA = TestDataUtil.givenUserA();
        User userB = TestDataUtil.givenUserB();
        User userC = TestDataUtil.givenUserC();
        userRepository.saveAll(List.of(userA, userB, userC));

        underTest.create(TestDataUtil.givenCategoryA(userA));
        underTest.create(TestDataUtil.givenCategoryB(userB));
        underTest.create(TestDataUtil.givenCategoryC(userC));

        Page<Category> result = underTest.getAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Food & Beverage", "Transportation", "Hobby");
    }

    @Test
    @DisplayName("3. Should partially update a category")
    public void shouldPartiallyUpdateCategory() {
        User user = TestDataUtil.givenUserA();
        userRepository.save(user);
        Category savedCategory = underTest.create(TestDataUtil.givenCategoryA(user));

        Category updateRequest = Category.builder()
                .name("Updated Name")
                .build();

        Category updatedCategory = underTest.updatePartial(savedCategory.getId(), updateRequest);

        assertThat(updatedCategory.getName()).isEqualTo("Updated Name");
        assertThat(updatedCategory.getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("4. Should delete category successfully")
    public void shouldDeleteCategory() {
        User user = TestDataUtil.givenUserA();
        userRepository.save(user);
        Category category = TestDataUtil.givenCategoryA(user);
        Category savedCategory = underTest.create(category);

        underTest.deleteById(savedCategory.getId());

        boolean stillExists = underTest.getAll(PageRequest.of(0, 10))
                .stream()
                .anyMatch(cat -> cat.getId().equals(savedCategory.getId()));

        assertThat(stillExists).isFalse();
    }

    @Test
    @DisplayName("5. Should return categories by user ID")
    public void shouldReturnCategoriesByUserId() {
        User user = TestDataUtil.givenUserA();
        userRepository.save(user);
        Category categoryA = underTest.create(TestDataUtil.givenCategoryA(user));
        Category categoryB = underTest.create(TestDataUtil.givenCategoryB(user));

        List<Category> result = underTest.getByUserId(user.getId());

        assertThat(result).containsExactlyInAnyOrder(categoryA, categoryB);
    }

    @Test
    @DisplayName("6. Should return empty list when no categories exist for user")
    public void shouldReturnEmptyListWhenNoCategories() {
        User user = TestDataUtil.givenUserA();
        userRepository.save(user);

        List<Category> result = underTest.getByUserId(user.getId());

        assertThat(result).isEmpty();
    }
}
