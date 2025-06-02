package com.dwi.expensetracker.domains.dtos.category;

import com.dwi.expensetracker.domains.dtos.user.UserBaseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryBaseDto {
    private Long id;

    private UserBaseDto customer;

    private String name;
}
