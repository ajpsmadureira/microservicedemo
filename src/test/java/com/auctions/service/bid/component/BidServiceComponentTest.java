package com.auctions.service.bid.component;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.bid.Bid;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class BidServiceComponentTest {

    @Mock
    UserRepository userRepository;

    @Mock
    AuctionRepository auctionRepository;

    @Mock
    BidRepository bidRepository;

    @Mock
    BidEntityToBidMapper bidEntityToBidMapper;

    User testUser;

    UserEntity testUserEntity;

    AuctionEntity testAuctionEntity;

    Bid testBid;

    BidEntity testBidEntity;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        Lot testLot = TestDataFactory.createTestLot(testUser);
        LotEntity testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        Auction testAuction = TestDataFactory.createTestAuction(testUser, testLot);
        testAuctionEntity = TestDataFactory.createTestAuctionEntity(testUserEntity, testLotEntity);

        testBid = TestDataFactory.createTestBid(testUser, testAuction);
        testBidEntity = TestDataFactory.createTestBidEntity(testUserEntity, testAuctionEntity);
    }
}
