package com.crm.util;

import com.crm.domain.BidState;
import com.crm.domain.Lot;
import com.crm.domain.LotState;
import com.crm.domain.User;
import com.crm.persistence.entity.BidEntity;
import com.crm.persistence.entity.LotEntity;
import com.crm.persistence.entity.UserEntity;

import java.math.BigDecimal;
import java.time.Instant;

public class TestDataFactory {

    private static final String TEST_USER_USERNAME = "testuser";
    private static final String TEST_USER_PASSWORD = "encodedPassword";
    private static final String TEST_USER_EMAIL = "test@example.com";

    private static final String ADMIN_USER_USERNAME = "admin";
    private static final String ADMIN_USER_PASSWORD = "encodedPassword";
    private static final String ADMIN_USER_EMAIL = "admin@example.com";

    private static final String LOT_USERNAME = "John";
    private static final String LOT_SURNAME = "Doe";
    private static final String LOT_PHOTO_URL = "file:photo.jpg";
    private static final Instant LOT_TIMESTAMP = Instant.ofEpochMilli(1739278311);

    private static final BigDecimal BID_AMOUNT = BigDecimal.valueOf(100);
    private static final Instant BID_TIMESTAMP = Instant.ofEpochMilli(1739278311);
    
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

    public static Lot createTestLot(User createdBy) {

        return Lot.builder()
                .name(LOT_USERNAME)
                .surname(LOT_SURNAME)
                .createdByUserId(createdBy.getId())
                .lastModifiedByUserId(createdBy.getId())
                .build();
    }

    public static LotEntity createTestLotEntity() {

        UserEntity userEntity = createTestUserEntity();

        LotEntity lotEntity = new LotEntity();

        lotEntity.setName(LOT_USERNAME);
        lotEntity.setSurname(LOT_SURNAME);
        lotEntity.setPhotoUrl(LOT_PHOTO_URL);
        lotEntity.setState(LotState.CREATED);
        lotEntity.setCreatedBy(userEntity);
        lotEntity.setLastModifiedBy(userEntity);
        lotEntity.setCreatedAt(LOT_TIMESTAMP);
        lotEntity.setUpdatedAt(LOT_TIMESTAMP);

        return lotEntity;
    }

    public static BidEntity createTestBidEntity() {

        UserEntity userEntity = createTestUserEntity();

        LotEntity lotEntity = createTestLotEntity();
        lotEntity.setCreatedBy(userEntity);
        lotEntity.setLastModifiedBy(userEntity);

        BidEntity bidEntity = new BidEntity();

        bidEntity.setAmount(BID_AMOUNT);
        bidEntity.setLot(lotEntity);
        bidEntity.setUntil(BID_TIMESTAMP);
        bidEntity.setState(BidState.OPENED);
        bidEntity.setCreatedBy(userEntity);
        bidEntity.setLastModifiedBy(userEntity);
        bidEntity.setCreatedAt(BID_TIMESTAMP);
        bidEntity.setUpdatedAt(BID_TIMESTAMP);

        return bidEntity;
    }
} 