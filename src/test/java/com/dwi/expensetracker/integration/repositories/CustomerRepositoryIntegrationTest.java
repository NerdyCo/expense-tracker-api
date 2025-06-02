package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;

@DataJpaTest
public class CustomerRepositoryIntegrationTest {
    private final UserRepository underTest;

    @Autowired
    public CustomerRepositoryIntegrationTest(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatCustomerCanBeCreatedAndRecalled() {
        User customer = TestDataUtil.createTestCustomerEntityA();
        underTest.save(customer);

        Optional<User> result = underTest.findById(customer.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(customer);
    }

    @Test
    public void testThatMultipleCustomersCanBeCreatedAndRecalled() {
        User customerA = TestDataUtil.createTestCustomerEntityA();
        User customerB = TestDataUtil.createTestCustomerEntityB();

        underTest.saveAll(List.of(customerA, customerB));

        Iterable<User> result = underTest.findAll();

        assertThat(result)
                .hasSize(2)
                .containsExactly(customerA, customerB);
    }
}
