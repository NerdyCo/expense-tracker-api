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
import org.springframework.test.annotation.DirtiesContext;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.services.CategoryService;
import com.dwi.expensetracker.services.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class CategoryServiceIntegrationTest {
    private final CategoryService underTest;
    private final UserService userService;

    @Autowired
    public CategoryServiceIntegrationTest(
            CategoryService underTest,
            UserService userService) {
        this.underTest = underTest;
        this.userService = userService;
    }

    @Test
    @DisplayName("1. Should create a category and retrieve it successfully")
    public void shouldCreateAndRetrieveCategory() {
        User user = userService.create(TestDataUtil.givenUserA());
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
        User userA = userService.create(TestDataUtil.givenUserA());
        User userB = userService.create(TestDataUtil.givenUserB());
        User userC = userService.create(TestDataUtil.givenUserC());

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
        User user = userService.create(TestDataUtil.givenUserA());
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
        User user = userService.create(TestDataUtil.givenUserA());
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
        User user = userService.create(TestDataUtil.givenUserA());
        Category categoryA = underTest.create(TestDataUtil.givenCategoryA(user));
        Category categoryB = underTest.create(TestDataUtil.givenCategoryB(user));

        List<Category> result = underTest.getByUserId(user.getId());

        assertThat(result).containsExactlyInAnyOrder(categoryA, categoryB);
    }
}
