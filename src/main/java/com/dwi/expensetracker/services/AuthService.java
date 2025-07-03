package com.dwi.expensetracker.services;

import com.dwi.expensetracker.domains.dtos.auth.RegisterUserDto;
import com.dwi.expensetracker.domains.dtos.auth.UpdateUserDto;
import com.dwi.expensetracker.domains.entities.User;

public interface AuthService {
    String registerUser(RegisterUserDto requestDto);

    String updateUser(String userId, UpdateUserDto requestDto);

    String deleteUser(String userId);

    User getUserById(String userId);
}
