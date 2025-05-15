package com.dwi.expensetracker;

import com.dwi.expensetracker.domains.entities.CustomerEntity;

public class TestDataUtil {
    public static CustomerEntity createTestCustomerEntityA() {
        return CustomerEntity.builder()
                .email("kautsar@gmail.com")
                .username("kautsar")
                .password("kautsar123")
                .build();
    }

    public static CustomerEntity createTestCustomerEntityB() {
        return CustomerEntity.builder()
                .email("teguh@gmail.com")
                .username("teguh")
                .password("teguh123")
                .build();
    }
}
