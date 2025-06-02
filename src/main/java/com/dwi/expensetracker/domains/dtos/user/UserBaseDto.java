package com.dwi.expensetracker.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserBaseDto {
    private Long id;

    private String username;

    private String email;
}
