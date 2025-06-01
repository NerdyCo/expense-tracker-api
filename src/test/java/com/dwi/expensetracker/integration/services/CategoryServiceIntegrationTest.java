package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.Customer;
import com.dwi.expensetracker.services.CategoryService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryServiceIntegrationTest {
    private final CategoryService underTest;

    @Autowired
    public CategoryServiceIntegrationTest(CategoryService underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatCategoryCanBeCreatedAndRecalled() {
        Customer customer = TestDataUtil.createTestCustomerEntityA();
        Category category = TestDataUtil.createTestCategoryEntityA(customer);

        Category savedCategory = underTest.save(category);

        Optional<Category> foundCategory = underTest.findOne(savedCategory.getId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Food & Beverage");
        assertThat(foundCategory.get().getCustomer().getUsername()).isEqualTo("kautsar");
    }

    @Test
    public void testThatMultipleCategoriesCaBeCreatedAndRecalled() {
        Customer customerA = TestDataUtil.createTestCustomerEntityA();
        Customer customerB = TestDataUtil.createTestCustomerEntityB();
        Customer customerC = TestDataUtil.createTestCustomerEntityC();

        underTest.save(TestDataUtil.createTestCategoryEntityA(customerA));
        underTest.save(TestDataUtil.createTestCategoryEntityB(customerB));
        underTest.save(TestDataUtil.createTestCategoryEntityC(customerC));

        Page<Category> result = underTest.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(Category::getName)
                .containsExactlyInAnyOrder("Food & Beverage", "Transportation", "Hobby");
    }

    @Test
    public void testThatCategoryCanBePartiallyUpdated() {
        Customer customer = TestDataUtil.createTestCustomerEntityA();
        Category savedCategory = underTest.save(TestDataUtil.createTestCategoryEntityA(customer));

        Category updateCategory = Category.builder()
                .name("updated name")
                .build();

        Category updatedCategory = underTest.partialUpdate(savedCategory.getId(), updateCategory);

        assertThat(updatedCategory.getName()).isEqualTo("updated name");
    }

    @Test
    public void testThatCategoryCanBeDeleted() {
        Customer customer = TestDataUtil.createTestCustomerEntityA();
        Category category = TestDataUtil.createTestCategoryEntityB(customer);
        Category savedCategory = underTest.save(category);

        underTest.delete(savedCategory.getId());

        boolean result = underTest.doesExist(savedCategory.getId());

        assertThat(result).isFalse();
    }
}
