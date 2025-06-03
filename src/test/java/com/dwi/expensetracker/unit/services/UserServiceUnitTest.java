package com.dwi.expensetracker.unit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.exceptions.DuplicateResourceException;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.impl.UserServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserServiceImpl")
public class UserServiceUnitTest {
    private static final UUID USER_ID = UUID.randomUUID();

    private User user;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        user = TestDataUtil.givenUserA();
        user.setId(USER_ID);
    }

    @Test
    @DisplayName("Should create a new user with encoded password")
    void shouldCreateUserSuccessfully() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);

        User created = userService.create(user);

        assertThat(created).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw when creating user with existing email")
    void shouldThrowWhenEmailExists() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.create(user))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email is already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all users paged")
    void shouldReturnPagedUsers() {
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(user);
    }

    @Test
    @DisplayName("Should get user by ID")
    void shouldReturnUserById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User found = userService.getById(USER_ID);

        assertThat(found).isEqualTo(user);
    }

    @Test
    @DisplayName("Should throw when user not found by ID")
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID");
    }

    @Test
    @DisplayName("Should update user partially")
    void shouldUpdateUserPartially() {
        User existing = TestDataUtil.givenUserA();
        existing.setId(USER_ID);

        User patch = TestDataUtil.givenUserB();
        patch.setPassword("newpass");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail(patch.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(patch.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(patch.getPassword())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.updatePartial(USER_ID, patch);

        assertThat(updated.getUsername()).isEqualTo(patch.getUsername());
        assertThat(updated.getEmail()).isEqualTo(patch.getEmail());
        assertThat(updated.getPassword()).isEqualTo("encoded");
    }

    @Test
    @DisplayName("Should delete user by ID")
    void shouldDeleteUserById() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        userService.deleteById(USER_ID);

        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent user")
    void shouldThrowWhenDeletingNonExistentUser() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID");
    }
}
