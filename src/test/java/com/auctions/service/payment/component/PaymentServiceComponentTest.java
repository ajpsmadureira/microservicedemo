package com.auctions.service.payment.component;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.payment.Payment;
import com.auctions.domain.user.User;
import com.auctions.mapper.payment.PaymentEntityToPaymentMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.PaymentEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.PaymentRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.payment.gateway.PaymentGateway;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public abstract class PaymentServiceComponentTest {

    static final Integer PAYMENT_ID = 1;

    @Mock
    UserRepository userRepository;

    @Mock
    PaymentRepository paymentRepository;

    @Mock
    AuctionRepository auctionRepository;

    @Mock
    PaymentGateway paymentGateway;

    @Mock
    PaymentEntityToPaymentMapper paymentEntityToPaymentMapper;

    PaymentEntity testPaymentEntity;

    UserEntity testUserEntity;

    AuctionEntity testAuctionEntity;

    User testUser;

    Payment testPayment;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();
        testUserEntity = TestDataFactory.createTestUserEntity();

        Lot testLot = TestDataFactory.createTestLot(testUser);
        LotEntity testLotEntity = TestDataFactory.createTestLotEntity(testUserEntity);

        Auction testAuction = TestDataFactory.createTestAuction(testUser, testLot);
        testAuctionEntity = TestDataFactory.createTestAuctionEntity(testUserEntity, testLotEntity);

        testPayment = TestDataFactory.createTestPayment(testAuction);
        testPaymentEntity = TestDataFactory.createTestPaymentEntity(testUserEntity, testAuctionEntity);
    }
}
