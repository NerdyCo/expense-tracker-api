package com.dwi.expensetracker.domains.dtos.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryRequestDto {
    @NotNull(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Category name is required")
    @Size(min = 3, max = 50, message = "Category name must be between {min} and {max} characters")
    private String name;
}
