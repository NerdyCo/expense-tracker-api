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
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.services.UserService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerServiceIntegrationTest {

    private final UserService underTest;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceIntegrationTest(
            UserService underTest,
            PasswordEncoder passwordEncoder) {
        this.underTest = underTest;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    public void testThatCustomerCanBeCreatedAndRecalled() {
        User customer = TestDataUtil.createTestCustomerEntityA();
        User savedCustomer = underTest.save(customer);

        Optional<User> foundCustomer = underTest.findOne(savedCustomer.getId());

        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getEmail()).isEqualTo("kautsar@gmail.com");
        assertThat(foundCustomer.get().getUsername()).isEqualTo("kautsar");
        assertThat(passwordEncoder.matches("kautsar123", foundCustomer.get().getPassword())).isTrue();
    }

    @Test
    public void testThatMultipleCustomersCanBeCreatedAndRecalled() {
        User customerA = TestDataUtil.createTestCustomerEntityA();
        User customerB = TestDataUtil.createTestCustomerEntityB();
        User customerC = TestDataUtil.createTestCustomerEntityC();

        underTest.save(customerA);
        underTest.save(customerB);
        underTest.save(customerC);

        Page<User> result = underTest.findAll(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .hasSize(3)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("kautsar", "dwi", "teguh");
    }

    @Test
    public void testThatCustomerCanBePartiallyUpdated() {
        User savedCustomer = underTest.save(TestDataUtil.createTestCustomerEntityA());
        User newCustomer = User.builder()
                .username("updated")
                .password("secret")
                .build();

        User updatedCustomer = underTest.partialUpdate(savedCustomer.getId(), newCustomer);

        assertThat(updatedCustomer.getUsername()).isEqualTo("updated");
        assertThat(updatedCustomer.getPassword()).isNotEqualTo("secret"); // because it's encoded
    }

    @Test
    public void testThatCustomerCanBeDeleted() {
        User savedcustomer = underTest.save(TestDataUtil.createTestCustomerEntityA());
        underTest.delete(savedcustomer.getId());

        boolean result = underTest.doesExist(savedcustomer.getId());

        assertThat(result).isFalse();
    }

}
