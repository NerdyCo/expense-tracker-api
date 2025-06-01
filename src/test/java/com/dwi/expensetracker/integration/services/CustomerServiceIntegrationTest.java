package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.Customer;
import com.dwi.expensetracker.services.CustomerService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerServiceIntegrationTest {

    private final CustomerService underTest;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceIntegrationTest(
            CustomerService underTest,
            PasswordEncoder passwordEncoder) {
        this.underTest = underTest;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    public void testThatCustomerCanBeCreatedAndRecalled() {
        Customer customer = TestDataUtil.createTestCustomerEntityA();
        Customer savedCustomer = underTest.save(customer);

        Optional<Customer> foundCustomer = underTest.findOne(savedCustomer.getId());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getEmail()).isEqualTo("kautsar@gmail.com");
        assertThat(foundCustomer.get().getUsername()).isEqualTo("kautsar");
        assertThat(passwordEncoder.matches("kautsar123", foundCustomer.get().getPassword())).isTrue();
    }

    @Test
    public void testThatMultipleCustomersCanBeCreatedAndRecalled() {
        Customer customerA = TestDataUtil.createTestCustomerEntityA();
        Customer customerB = TestDataUtil.createTestCustomerEntityB();
        Customer customerC = TestDataUtil.createTestCustomerEntityC();

        underTest.save(customerA);
        underTest.save(customerB);
        underTest.save(customerC);

        Page<Customer> result = underTest.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(Customer::getUsername)
                .containsExactlyInAnyOrder("kautsar", "dwi", "teguh");
    }

    @Test
    public void testThatCustomerCanBePartiallyUpdated() {
        Customer savedCustomer = underTest.save(TestDataUtil.createTestCustomerEntityA());
        Customer newCustomer = Customer.builder()
                .username("updated")
                .password("secret")
                .build();

        Customer updatedCustomer = underTest.partialUpdate(savedCustomer.getId(), newCustomer);

        assertThat(updatedCustomer.getUsername()).isEqualTo("updated");
        assertThat(updatedCustomer.getPassword()).isNotEqualTo("secret"); // because it's encoded
    }

    @Test
    public void testThatCustomerCanBeDeleted() {
        Customer savedcustomer = underTest.save(TestDataUtil.createTestCustomerEntityA());
        underTest.delete(savedcustomer.getId());

        boolean result = underTest.doesExist(savedcustomer.getId());

        assertThat(result).isFalse();
    }

}
