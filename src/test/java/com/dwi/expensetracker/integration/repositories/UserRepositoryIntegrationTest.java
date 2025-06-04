package com.dwi.expensetracker.integration.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;

@DataJpaTest
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserRepositoryIntegrationTest {
    private final UserRepository underTest;

    @Autowired
    public UserRepositoryIntegrationTest(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    @DisplayName("1. Should save and retrieve user by ID")
    public void shouldSaveAndRetrieveUserById() {
        User user = TestDataUtil.givenUserA();

        underTest.save(user);
        Optional<User> result = underTest.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    @DisplayName("2. Should save multiple users and retrieve all")
    public void shouldSaveMultipleUsersAndRetrieveAll() {
        User userA = TestDataUtil.givenUserA();
        User userB = TestDataUtil.givenUserB();

        underTest.saveAll(List.of(userA, userB));
        List<User> result = underTest.findAll();

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(userA, userB);
    }

    @Test
    @DisplayName("3. Should return true if user exists by email")
    public void shouldCheckUserExistsByEmail() {
        User user = TestDataUtil.givenUserA();
        underTest.save(user);

        boolean exists = underTest.existsByEmail("kautsar@gmail.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("4. Should return true if user exists by username")
    public void shouldCheckUserExistsByUsername() {
        User user = TestDataUtil.givenUserA();
        underTest.save(user);

        boolean exists = underTest.existsByUsername("kautsar");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("5. Should return false when user does not exist by email or username")
    public void shouldReturnFalseIfUserDoesNotExist() {
        boolean emailExists = underTest.existsByEmail("nonexistent@email.com");
        boolean usernameExists = underTest.existsByUsername("nonexistent");

        assertThat(emailExists).isFalse();
        assertThat(usernameExists).isFalse();
    }
}
