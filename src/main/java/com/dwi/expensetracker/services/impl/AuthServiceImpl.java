package com.dwi.expensetracker.services.impl;

import java.util.Collections;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import com.dwi.expensetracker.domains.dtos.auth.RegisterUserDto;
import com.dwi.expensetracker.domains.dtos.auth.UpdateUserDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.exceptions.DuplicateResourceException;
import com.dwi.expensetracker.repositories.UserRepository;
import com.dwi.expensetracker.services.AuthService;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final Keycloak keycloak;
    private final UserRepository userRepository;

    private String realm = "expense-realm";

    @Override
    @Transactional
    public String registerUser(RegisterUserDto requestDto) {
        // Check for duplicate username or email
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        // Create user in Keycloak
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(requestDto.getUsername());
        userRep.setEmail(requestDto.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(false);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(requestDto.getPassword());
        credential.setTemporary(false);
        userRep.setCredentials(Collections.singletonList(credential));

        // check username and email is already in keycloak or not
        if (keycloak.realm(realm).users().search(userRep.getUsername()).size() > 0) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (keycloak.realm(realm).users().search(userRep.getEmail()).size() > 0) {
            throw new DuplicateResourceException("Email already exists");
        }

        Response response = keycloak.realm(realm).users().create(userRep);

        if (response.getStatus() != 201) {
            throw new IllegalStateException("Keycloak registration failed: " + response.getStatusInfo());
        }

        // Get the Keycloak user ID
        String keycloakUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // Assign USER role
        try {
            RoleRepresentation userRole = keycloak.realm(realm).roles().get("USER").toRepresentation();
            log.info("Attempting to assign role: {}", userRole.getName());
            keycloak.realm(realm).users().get(keycloakUserId)
                    .roles().realmLevel()
                    .add(Collections.singletonList(userRole));
            log.info("Role assigned successfully to user ID: {}", keycloakUserId);
        } catch (Exception e) {
            log.error("Failed to assign role to user ID {}: {}", keycloakUserId, e.getMessage());
            throw new IllegalStateException("Failed to assign USER role: " + e.getMessage());
        }

        // Create user in local database
        User user = User.builder()
                .id(keycloakUserId)
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .build();
        userRepository.save(user);

        return "User registered successfully";
    }

    @Override
    @Transactional
    public String updateUser(String userId, UpdateUserDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Update Keycloak user
        UserRepresentation userRep = keycloak.realm(realm).users().get(userId).toRepresentation();
        boolean updated = false;

        if (requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            if (!requestDto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(requestDto.getEmail())) {
                throw new DuplicateResourceException("Email already exists");
            }
            userRep.setEmail(requestDto.getEmail());
            user.setEmail(requestDto.getEmail());
            updated = true;
        }
        if (requestDto.getUsername() != null && !requestDto.getUsername().isBlank()) {
            if (!requestDto.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(requestDto.getUsername())) {
                throw new DuplicateResourceException("Username already exists");
            }
            userRep.setUsername(requestDto.getUsername());
            user.setUsername(requestDto.getUsername());
            updated = true;
        }

        if (updated) {
            keycloak.realm(realm).users().get(userId).update(userRep);
            userRepository.save(user);
            return "User updated successfully";
        }

        return "No changes provided";
    }

    @Override
    @Transactional
    public String deleteUser(String userId) {
        userRepository.deleteById(userId);
        keycloak.realm(realm).users().get(userId).remove();
        return "User deleted successfully";
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with ID " + userId));
    }

}
