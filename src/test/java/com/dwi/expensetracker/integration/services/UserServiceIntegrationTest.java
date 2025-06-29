package com.dwi.expensetracker.integration.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.UserService;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Integration tests for UserServiceImpl")
public class UserServiceIntegrationTest {

    private final UserService underTest;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceIntegrationTest(UserService underTest, UserRepository userRepository) {
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    @DisplayName("1. Should retrieve user by ID successfully")
    public void shouldRetrieveUserById() {
        User user = TestDataUtil.givenUserA();
        userRepository.save(user);

        User foundUser = underTest.getById(user.getId());

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getEmail()).isEqualTo("kautsar@gmail.com");
        assertThat(foundUser.getUsername()).isEqualTo("kautsar");
    }

    @Test
    @DisplayName("2. Should throw when user not found by ID")
    public void shouldThrowWhenUserNotFound() {
        String nonExistentId = TestDataUtil.USER_ID_B;

        assertThatThrownBy(() -> underTest.getById(nonExistentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID " + nonExistentId);
    }

    @Test
    @DisplayName("3. Should retrieve all users paged")
    public void shouldRetrieveAllUsersPaged() {
        userRepository.save(TestDataUtil.givenUserA());
        userRepository.save(TestDataUtil.givenUserB());
        userRepository.save(TestDataUtil.givenUserC());

        Page<User> users = underTest.getAll(PageRequest.of(0, 10));

        assertThat(users.getContent())
                .hasSize(3)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("kautsar", "teguh", "dwi");
    }

    @Test
    @DisplayName("4. Should return empty page when no users exist")
    public void shouldReturnEmptyPageWhenNoUsers() {
        Page<User> users = underTest.getAll(PageRequest.of(0, 10));

        assertThat(users.getContent()).isEmpty();
    }
}