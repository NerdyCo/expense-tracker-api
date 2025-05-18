package com.dwi.expensetracker.domains.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCustomerDto {
    private Long id;

    private String username;

    private String email;

    private String password;
}
