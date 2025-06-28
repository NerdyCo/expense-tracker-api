package com.dwi.expensetracker.controllers;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.springframework.http.HttpStatus;
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
import com.dwi.expensetracker.exceptions.DuplicateResourceException;
import com.dwi.expensetracker.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterUserDto requestDto) {
        try {
            String result = authService.registerUser(requestDto);
            return ResponseEntity.ok(result);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/users/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> updateUser(@Valid @RequestBody UpdateUserDto requestDto) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        try {
            String result = authService.updateUser(userId, requestDto);
            return ResponseEntity.ok(result);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/users/delete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteUser() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String result = authService.deleteUser(userId);

        return ResponseEntity.ok(result);
    }
}
