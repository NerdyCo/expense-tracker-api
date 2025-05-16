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
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
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
        CustomerEntity customer = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity category = TestDataUtil.createTestCategoryEntityA(customer);

        CategoryEntity savedCategory = underTest.save(category);

        Optional<CategoryEntity> foundCategory = underTest.findOne(savedCategory.getId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Food & Beverage");
        assertThat(foundCategory.get().getCustomer().getUsername()).isEqualTo("kautsar");
    }

    @Test
    public void testThatMultipleCategoriesCaBeCreatedAndRecalled() {
        CustomerEntity customerA = TestDataUtil.createTestCustomerEntityA();
        CustomerEntity customerB = TestDataUtil.createTestCustomerEntityB();
        CustomerEntity customerC = TestDataUtil.createTestCustomerEntityC();

        underTest.save(TestDataUtil.createTestCategoryEntityA(customerA));
        underTest.save(TestDataUtil.createTestCategoryEntityB(customerB));
        underTest.save(TestDataUtil.createTestCategoryEntityC(customerC));

        Page<CategoryEntity> result = underTest.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(CategoryEntity::getName)
                .containsExactlyInAnyOrder("Food & Beverage", "Transportation", "Hobby");
    }

    @Test
    public void testThatCategoryCanBePartiallyUpdated() {
        CustomerEntity customer = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity savedCategory = underTest.save(TestDataUtil.createTestCategoryEntityA(customer));

        CategoryEntity updateCategory = CategoryEntity.builder()
                .name("updated name")
                .build();

        CategoryEntity updatedCategory = underTest.partialUpdate(savedCategory.getId(), updateCategory);

        assertThat(updatedCategory.getName()).isEqualTo("updated name");
    }

    @Test
    public void testThatCategoryCanBeDeleted() {
        CustomerEntity customer = TestDataUtil.createTestCustomerEntityA();
        CategoryEntity category = TestDataUtil.createTestCategoryEntityB(customer);
        CategoryEntity savedCategory = underTest.save(category);

        underTest.delete(savedCategory.getId());

        boolean result = underTest.doesExist(savedCategory.getId());

        assertThat(result).isFalse();
    }
}
