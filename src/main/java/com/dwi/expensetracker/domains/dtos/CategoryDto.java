package com.dwi.expensetracker.domains.dtos;

import com.dwi.expensetracker.domains.entities.CustomerEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long id;

    private CustomerEntity customer;

    private String name;
}
