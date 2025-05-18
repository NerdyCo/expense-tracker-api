package com.dwi.expensetracker.domains.dtos.category;

import com.dwi.expensetracker.domains.dtos.customer.CustomerDto;

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

    private CustomerDto customer;

    private String name;
}
