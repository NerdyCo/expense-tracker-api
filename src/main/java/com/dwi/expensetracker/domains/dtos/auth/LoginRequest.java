package com.dwi.expensetracker.domains.dtos.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Size(min = 6, message = "Password must be at least {min} characters")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
