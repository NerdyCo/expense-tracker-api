package com.dwi.expensetracker.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
public class InMemoryTokenBlacklistService implements TokenBlacklistService {

    private final Set<String> blacklistedTokens = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

}
