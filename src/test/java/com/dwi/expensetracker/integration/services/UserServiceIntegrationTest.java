package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserServiceIntegrationTest {

    private final UserService underTest;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceIntegrationTest(
            UserService underTest,
            PasswordEncoder passwordEncoder) {
        this.underTest = underTest;
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @DisplayName("1. Should create a user and retrieve it successfully")
    public void shouldCreateAndRetrieveUser() {
        User user = TestDataUtil.givenUserA();

        User savedUser = underTest.create(user);
        Optional<User> foundUser = Optional.of(underTest.getById(savedUser.getId()));

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("kautsar@gmail.com");
        assertThat(foundUser.get().getUsername()).isEqualTo("kautsar");
        assertThat(passwordEncoder.matches("password123", foundUser.get().getPassword())).isTrue();
    }

    @Test
    @DisplayName("2. Should create multiple users and retrieve all")
    public void shouldCreateMultipleUsersAndRetrieveAll() {
        underTest.create(TestDataUtil.givenUserA());
        underTest.create(TestDataUtil.givenUserB());
        // underTest.create(TestDataUtil.givenUserC());

        Page<User> users = underTest.getAll(PageRequest.of(0, 10));

        assertThat(users.getContent())
                .hasSize(3)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("kautsar", "teguh", "Test User");
    }

    @Test
    @DisplayName("3. Should partially update user successfully")
    public void shouldPartiallyUpdateUser() {
        User savedUser = underTest.create(TestDataUtil.givenUserA());
        User updateRequest = User.builder()
                .username("updated")
                .password("secret")
                .build();

        User updatedUser = underTest.updatePartial(savedUser.getId(), updateRequest);

        assertThat(updatedUser.getUsername()).isEqualTo("updated");
        assertThat(passwordEncoder.matches("secret", updatedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("4. Should delete user successfully")
    public void shouldDeleteUser() {
        User savedUser = underTest.create(TestDataUtil.givenUserA());

        underTest.deleteById(savedUser.getId());
        boolean exists = underTest.getAll(PageRequest.of(0, 10))
                .stream()
                .anyMatch(user -> user.getId().equals(savedUser.getId()));

        assertThat(exists).isFalse();
    }
}
