package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Category;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.CategoryRepository;
import com.dwi.expensetracker.repositories.UserRepository;

@DataJpaTest
public class CategoryRepositoryIntegrationTest {
    private final CategoryRepository underTest;
    private final UserRepository customerRepository;

    @Autowired
    public CategoryRepositoryIntegrationTest(CategoryRepository underTest, UserRepository customerRepository) {
        this.underTest = underTest;
        this.customerRepository = customerRepository;
    }

    @Test
    public void testThatCategoryCanBeCreatedAndRecalled() {
        User customer = customerRepository.save(TestDataUtil.createTestCustomerEntityA());
        Category category = TestDataUtil.createTestCategoryEntityA(customer);

        underTest.save(category);

        Optional<Category> result = underTest.findById(category.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Food & Beverage");
        assertThat(result.get().getCustomer()).isEqualTo(customer);
    }

    @Test
    public void testThatMultipleCategoriesCanBeCreatedAndRecalled() {
        User customer = customerRepository.save(TestDataUtil.createTestCustomerEntityA());

        Category categoryA = TestDataUtil.createTestCategoryEntityA(customer);
        Category categoryB = TestDataUtil.createTestCategoryEntityB(customer);
        Category categoryC = TestDataUtil.createTestCategoryEntityC(customer);

        underTest.saveAll(List.of(categoryA, categoryB, categoryC));

        Iterable<Category> result = underTest.findAll();

        assertThat(result).hasSize(3).containsExactly(categoryA, categoryB, categoryC);
    }
}
