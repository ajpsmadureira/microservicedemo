package com.crm.util;

import com.crm.domain.Customer;
import com.crm.domain.User;
import com.crm.persistence.entity.UserEntity;

public class TestDataFactory {
    
    public static User createTestUser() {

        return User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .isAdmin(false)
                .isActive(true)
                .build();
    }

    public static UserEntity createTestUserEntity() {

        UserEntity userEntity = new UserEntity();

        userEntity.setActive(true);
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setId(1L);
        userEntity.setAdmin(false);

        return userEntity;
    }

    public static User createTestAdmin() {

        return User.builder()
                .username("admin")
                .email("admin@example.com")
                .password("encodedPassword")
                .isAdmin(true)
                .isActive(true)
                .build();
    }

    public static Customer createTestCustomer(User createdBy) {

        return Customer.builder()
                .name("John")
                .surname("Doe")
                .createdById(createdBy.getId())
                .lastModifiedById(createdBy.getId())
                .build();
    }
} 