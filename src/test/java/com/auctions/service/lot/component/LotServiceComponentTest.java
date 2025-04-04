package com.auctions.service.lot.component;

import com.auctions.domain.Lot;
import com.auctions.domain.User;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.filestorage.FileStorageService;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class LotServiceComponentTest {

    static final Integer LOT_ID = 1;

    @Mock
    UserRepository userRepository;

    @Mock
    LotRepository lotRepository;

    @Mock
    FileStorageService fileStorageService;

    @Mock
    LotEntityToLotMapper lotEntityToLotMapper;

    Lot testLot;

    LotEntity testLotEntity;

    AuctionEntity testAuctionEntity;

    UserEntity testUserEntity;

    User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        testLot = TestDataFactory.createTestLot(testUser);
        testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        testAuctionEntity = TestDataFactory.createTestAuctionEntity(testUserEntity, testLotEntity);
    }
}
