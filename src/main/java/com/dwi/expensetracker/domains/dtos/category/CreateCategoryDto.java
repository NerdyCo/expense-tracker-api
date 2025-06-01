package com.dwi.expensetracker.domains.dtos.category;

import com.dwi.expensetracker.domains.dtos.customer.CreateCustomerDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryDto {
    private Long id;

    private CreateCustomerDto customer;

    private String name;
}
