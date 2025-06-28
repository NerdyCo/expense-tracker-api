package com.dwi.expensetracker.controllers;

import java.util.Collections;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.auth.RegisterUserDto;
import com.dwi.expensetracker.domains.dtos.auth.UpdateUserDto;
import com.dwi.expensetracker.domains.entities.User;
import com.dwi.expensetracker.repositories.UserRepository;

import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final Keycloak keycloak;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserDto requestDto) {
        // Check for duplicate username or email
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Create user in Keycloak
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(requestDto.getUsername());
        userRep.setEmail(requestDto.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(false); // Require email verification in production

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(requestDto.getPassword());
        credential.setTemporary(false);
        userRep.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm("expense-realm").users().create(userRep);
        if (response.getStatus() != 201) {
            return ResponseEntity.status(response.getStatus()).body("Keycloak registration failed");
        }

        // Get the Keycloak user ID from the response
        String keycloakUserId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");

        // assign USER role
        keycloak.realm("expense-realm").users().get(keycloakUserId)
                .roles().realmLevel()
                .add(Collections.singletonList(
                        keycloak.realm("expense-realm").roles().get("USER").toRepresentation()));

        // Create user in local database
        User user = User.builder()
                .id(keycloakUserId)
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .build();
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PutMapping("/users/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UpdateUserDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName(); // Keycloak user ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Update Keycloak user
        UserRepresentation userRep = keycloak.realm("expense-realm").users().get(userId).toRepresentation();
        boolean updated = false;

        if (requestDto.getEmail() != null && !requestDto.getEmail().isBlank()) {
            if (!requestDto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(requestDto.getEmail())) {
                return ResponseEntity.badRequest().body("Email already exists");
            }
            userRep.setEmail(requestDto.getEmail());
            user.setEmail(requestDto.getEmail());
            updated = true;
        }
        if (requestDto.getUsername() != null && !requestDto.getUsername().isBlank()) {
            if (!requestDto.getUsername().equals(user.getUsername())
                    && userRepository.existsByUsername(requestDto.getUsername())) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            userRep.setUsername(requestDto.getUsername());
            user.setUsername(requestDto.getUsername());
            updated = true;
        }

        if (updated) {
            keycloak.realm("expense-realm").users().get(userId).update(userRep);
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        }

        return ResponseEntity.ok("No changes provided");
    }

    @DeleteMapping("/users/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userRepository.deleteById(userId);
        keycloak.realm("expense-realm").users().get(userId).remove();
        return ResponseEntity.ok("User deleted successfully");
    }
}
