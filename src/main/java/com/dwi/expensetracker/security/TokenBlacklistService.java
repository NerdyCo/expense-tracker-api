package com.dwi.expensetracker.security;

public interface TokenBlacklistService {
    void blacklistToken(String token);

    boolean isTokenBlacklisted(String token);
}
