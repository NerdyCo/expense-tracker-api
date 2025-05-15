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
import com.dwi.expensetracker.services.CustomerService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryServiceIntegrationTest {
    private final CategoryService underTest;
    private final CustomerService customerService;

    @Autowired
    public CategoryServiceIntegrationTest(CategoryService underTest, CustomerService customerService) {
        this.underTest = underTest;
        this.customerService = customerService;
    }

    @Test
    public void testThatCategoryCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = TestDataUtil.createTestCategoryEntityA(customer);

        CategoryEntity savedCategory = underTest.save(category);

        Optional<CategoryEntity> foundCategory = underTest.findOne(savedCategory.getId());

        assertThat(foundCategory).isPresent();
        assertThat(foundCategory.get().getName()).isEqualTo("Food & Beverage");
        assertThat(foundCategory.get().getCustomer().getUsername()).isEqualTo("kautsar");
    }

    @Test
    public void testThatMultipleCategoriesCaBeCreatedAndRecalled() {
        CustomerEntity customer = TestDataUtil.createTestCustomerEntityA();

        underTest.save(TestDataUtil.createTestCategoryEntityA(customer));
        underTest.save(TestDataUtil.createTestCategoryEntityB(customer));
        underTest.save(TestDataUtil.createTestCategoryEntityC(customer));

        Page<CategoryEntity> result = underTest.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(CategoryEntity::getName)
                .containsExactlyInAnyOrder("Food & Beverage", "Transportation", "Hobby");
    }

    @Test
    public void testThatCategoryCanBePartiallyUpdated() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity savedCategory = underTest.save(TestDataUtil.createTestCategoryEntityA(customer));

        CategoryEntity updateCategory = CategoryEntity.builder()
                .name("updated name")
                .build();

        CategoryEntity updatedCategory = underTest.partialUpdate(savedCategory.getId(), updateCategory);

        assertThat(updatedCategory.getName()).isEqualTo("updated name");
    }

    @Test
    public void testThatCategoryCanBeDeleted() {
        CustomerEntity customer = customerService.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity savedcategory = TestDataUtil.createTestCategoryEntityB(customer);

        underTest.delete(savedcategory.getId());

        boolean result = underTest.doesExist(savedcategory.getId());

        assertThat(result).isFalse();
    }
}
