package com.dwi.expensetracker.domains.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.dwi.expensetracker.domains.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long id;

    private CustomerDto customer;

    private CategoryDto category;

    private BigDecimal amount;

    private TransactionType type;

    private String description;

    private LocalDate date;
}
