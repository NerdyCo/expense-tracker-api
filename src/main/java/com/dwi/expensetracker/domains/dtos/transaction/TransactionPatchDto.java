package com.dwi.expensetracker.domains.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.dwi.expensetracker.domains.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionPatchDto {
    private UUID categoryId;

    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be at least {value}")
    private BigDecimal amount;

    private TransactionType type;

    @Size(max = 500, message = "Description must not exceed {max} characters")
    private String description;

    private LocalDate date;
}
