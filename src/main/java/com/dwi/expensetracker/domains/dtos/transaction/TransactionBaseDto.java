package com.dwi.expensetracker.domains.dtos.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.dwi.expensetracker.domains.dtos.category.CategoryBaseDto;
import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;
import com.dwi.expensetracker.domains.enums.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionBaseDto {

    private UUID id;

    private UserBaseDto customer;

    private CategoryBaseDto category;

    private BigDecimal amount;

    private TransactionType type;

    private String description;

    private LocalDate date;
}
