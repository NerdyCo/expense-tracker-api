package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Customer;
import com.dwi.expensetracker.repositories.CustomerRepository;

@DataJpaTest
public class CustomerRepositoryIntegrationTest {
    private final CustomerRepository underTest;

    @Autowired
    public CustomerRepositoryIntegrationTest(CustomerRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatCustomerCanBeCreatedAndRecalled() {
        Customer customer = TestDataUtil.createTestCustomerEntityA();
        underTest.save(customer);

        Optional<Customer> result = underTest.findById(customer.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(customer);
    }

    @Test
    public void testThatMultipleCustomersCanBeCreatedAndRecalled() {
        Customer customerA = TestDataUtil.createTestCustomerEntityA();
        Customer customerB = TestDataUtil.createTestCustomerEntityB();

        underTest.saveAll(List.of(customerA, customerB));

        Iterable<Customer> result = underTest.findAll();

        assertThat(result)
                .hasSize(2)
                .containsExactly(customerA, customerB);
    }
}
