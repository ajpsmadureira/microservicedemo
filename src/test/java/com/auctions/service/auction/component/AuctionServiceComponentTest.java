package com.auctions.service.auction.component;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class AuctionServiceComponentTest {

    static final Integer AUCTION_ID = 1;

    @Mock
    UserRepository userRepository;

    @Mock
    LotRepository lotRepository;

    @Mock
    AuctionRepository auctionRepository;

    @Mock
    AuctionEntityToAuctionMapper auctionEntityToAuctionMapper;

    Auction testAuction;

    AuctionEntity testAuctionEntity;

    UserEntity testUserEntity;

    User testUser;

    LotEntity testLotEntity;

    Lot testLot;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        testLot = TestDataFactory.createTestLot(testUser);
        testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        testAuction = TestDataFactory.createTestAuction(testUser, testLot);
        testAuctionEntity = TestDataFactory.createTestAuctionEntity(testUserEntity, testLotEntity);
    }
}
