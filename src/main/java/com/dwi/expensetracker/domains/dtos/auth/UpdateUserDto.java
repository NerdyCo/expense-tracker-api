package com.dwi.expensetracker.domains.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserDto {
    @Size(min = 3, max = 50, message = "Username must be between {min} and {max} characters")
    private String username;

    @Email(message = "Email must be valid")
    private String email;
}
