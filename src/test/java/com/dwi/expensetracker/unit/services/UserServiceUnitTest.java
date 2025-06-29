package com.dwi.expensetracker.unit.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

import com.dwi.expensetracker.TestDataUtil;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.impl.UserServiceImpl;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for UserServiceImpl")
public class UserServiceUnitTest {
    private static final String USER_ID = TestDataUtil.USER_ID_A;

    private User user;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setup() {
        user = TestDataUtil.givenUserA();
    }

    @Test
    @DisplayName("Should get all users paged")
    public void shouldReturnPagedUsers() {
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<User> page = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(user);
    }

    @Test
    @DisplayName("Should get empty page when no users exist")
    public void shouldReturnEmptyPageWhenNoUsers() {
        PageRequest pageable = PageRequest.of(0, 10);
        PageImpl<User> emptyPage = new PageImpl<>(Collections.emptyList());

        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<User> result = userService.getAll(pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("Should get user by ID")
    public void shouldReturnUserById() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        User found = userService.getById(USER_ID);

        assertThat(found).isEqualTo(user);
    }

    @Test
    @DisplayName("Should throw when user not found by ID")
    public void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(USER_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with ID " + USER_ID);
    }
}
