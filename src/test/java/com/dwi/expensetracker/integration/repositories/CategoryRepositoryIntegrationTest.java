package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.CategoryEntity;
import com.dwi.expensetracker.domains.entities.CustomerEntity;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.CustomerRepository;

@DataJpaTest
public class CategoryRepositoryIntegrationTest {
    private final CategoryRepository underTest;
    private final CustomerRepository customerRepository;

    @Autowired
    public CategoryRepositoryIntegrationTest(CategoryRepository underTest, CustomerRepository customerRepository) {
        this.underTest = underTest;
        this.customerRepository = customerRepository;
    }

    @Test
    public void testThatCategoryCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerRepository.save(TestDataUtil.createTestCustomerEntityA());
        CategoryEntity category = TestDataUtil.createTestCategoryEntityA(customer);

        underTest.save(category);

        Optional<CategoryEntity> result = underTest.findById(category.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Food & Beverage");
        assertThat(result.get().getCustomer()).isEqualTo(customer);
    }

    @Test
    public void testThatMultipleCategoriesCanBeCreatedAndRecalled() {
        CustomerEntity customer = customerRepository.save(TestDataUtil.createTestCustomerEntityA());

        CategoryEntity categoryA = TestDataUtil.createTestCategoryEntityA(customer);
        CategoryEntity categoryB = TestDataUtil.createTestCategoryEntityB(customer);
        CategoryEntity categoryC = TestDataUtil.createTestCategoryEntityC(customer);

        underTest.saveAll(List.of(categoryA, categoryB, categoryC));

        Iterable<CategoryEntity> result = underTest.findAll();

        assertThat(result).hasSize(3).containsExactly(categoryA, categoryB, categoryC);
    }
}
