package com.dwi.expensetracker.services;

import org.springframework.security.core.userdetails.UserDetails;

import com.dwi.expensetracker.domains.dtos.auth.AuthResponse;

public interface AuthenticationService {
    UserDetails authenticate(String email, String password);

    AuthResponse generateTokens(UserDetails userDetails);

    UserDetails validateToken(String token);

    AuthResponse refreshToken(String refreshToken);
}
