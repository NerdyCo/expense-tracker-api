package com.dwi.expensetracker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dwi.expensetracker.domains.dtos.auth.AuthResponse;
import com.dwi.expensetracker.domains.dtos.auth.LoginRequest;
import com.dwi.expensetracker.domains.dtos.auth.RefreshTokenRequest;
import com.dwi.expensetracker.security.TokenBlacklistService;
import com.dwi.expensetracker.services.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword());

        AuthResponse authResponse = authenticationService.generateTokens(userDetails);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        AuthResponse authResponse = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody(required = false) RefreshTokenRequest request) {
        String accessToken = authorizationHeader.substring(7);
        tokenBlacklistService.blacklistToken(accessToken);

        if (request != null && request.getRefreshToken() != null) {
            tokenBlacklistService.blacklistToken(request.getRefreshToken());
        }

        return ResponseEntity.noContent().build();
    }
}
