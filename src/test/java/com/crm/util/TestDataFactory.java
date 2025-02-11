package com.crm.util;

import com.crm.domain.Customer;
import com.crm.domain.User;
import com.crm.persistence.entity.CustomerEntity;
import com.crm.persistence.entity.UserEntity;

import java.time.Instant;

public class TestDataFactory {

    private static final String TEST_USER_USERNAME = "testuser";
    private static final String TEST_USER_PASSWORD = "encodedPassword";
    private static final String TEST_USER_EMAIL = "test@example.com";

    private static final String ADMIN_USER_USERNAME = "admin";
    private static final String ADMIN_USER_PASSWORD = "encodedPassword";
    private static final String ADMIN_USER_EMAIL = "admin@example.com";

    private static final String CUSTOMER_USERNAME = "John";
    private static final String CUSTOMER_SURNAME = "Doe";
    private static final String CUSTOMER_PHOTO_URL = "file:photo.jpg";
    private static final Instant CUSTOMER_TIMESTAMP = Instant.ofEpochMilli(1739278311);
    
    public static User createTestUser() {

        return User.builder()
                .username(TEST_USER_USERNAME)
                .email(TEST_USER_EMAIL)
                .password(TEST_USER_PASSWORD)
                .isAdmin(false)
                .isActive(true)
                .build();
    }

    public static UserEntity createTestUserEntity() {

        UserEntity userEntity = new UserEntity();

        userEntity.setActive(true);
        userEntity.setUsername(TEST_USER_USERNAME);
        userEntity.setEmail(TEST_USER_EMAIL);
        userEntity.setPassword(TEST_USER_PASSWORD);
        userEntity.setId(1);
        userEntity.setAdmin(false);

        return userEntity;
    }

    public static User createTestAdmin() {

        return User.builder()
                .username(ADMIN_USER_USERNAME)
                .email(ADMIN_USER_EMAIL)
                .password(ADMIN_USER_PASSWORD)
                .isAdmin(true)
                .isActive(true)
                .build();
    }

    public static Customer createTestCustomer(User createdBy) {

        return Customer.builder()
                .name(CUSTOMER_USERNAME)
                .surname(CUSTOMER_SURNAME)
                .createdByUserId(createdBy.getId())
                .lastModifiedByUserId(createdBy.getId())
                .build();
    }

    public static CustomerEntity createTestCustomerEntity() {

        UserEntity userEntity = createTestUserEntity();

        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setName(CUSTOMER_USERNAME);
        customerEntity.setSurname(CUSTOMER_SURNAME);
        customerEntity.setPhotoUrl(CUSTOMER_PHOTO_URL);
        customerEntity.setCreatedBy(userEntity);
        customerEntity.setLastModifiedBy(userEntity);
        customerEntity.setCreatedAt(CUSTOMER_TIMESTAMP);
        customerEntity.setUpdatedAt(CUSTOMER_TIMESTAMP);

        return customerEntity;
    }
} 