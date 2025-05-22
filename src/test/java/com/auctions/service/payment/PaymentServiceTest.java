package com.auctions.service.payment;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.payment.Payment;
import com.auctions.domain.user.User;
import com.auctions.service.payment.component.CancelPaymentServiceComponent;
import com.auctions.service.payment.component.CreatePaymentServiceComponent;
import com.auctions.service.payment.component.GetPaymentServiceComponent;
import com.auctions.util.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private CreatePaymentServiceComponent createPaymentServiceComponent;

    @Mock
    private GetPaymentServiceComponent getPaymentServiceComponent;

    @Mock
    private CancelPaymentServiceComponent cancelPaymentServiceComponent;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;

    private User testUser;

    @BeforeEach
    void setUp() {

        testUser = TestDataFactory.createTestUser();

        Lot testLot = TestDataFactory.createTestLot(testUser);

        Auction testAuction = TestDataFactory.createTestAuction(testUser, testLot);

        testPayment = TestDataFactory.createTestPayment(testAuction);
    }

    @Test
    void createPayment() {

        when(createPaymentServiceComponent.createPayment(testPayment, testUser)).thenReturn(testPayment);

        assertEquals(testPayment, paymentService.createPayment(testPayment, testUser));
    }

    @Test
    void getPaymentById() {

        when(getPaymentServiceComponent.getPaymentById(testPayment.getId())).thenReturn(testPayment);

        assertEquals(testPayment, paymentService.getPaymentById(testPayment.getId()));
    }

    @Test
    void getPaymentByAuctionId() {

        when(getPaymentServiceComponent.getPaymentsByAuctionId(testPayment.getId())).thenReturn(List.of(testPayment));

        assertEquals(testPayment, paymentService.getPaymentsByAuctionId(testPayment.getAuctionId()).get(0));
    }

    @Test
    void cancelPayment() {

        paymentService.cancelPayment(testPayment.getId());

        verify(cancelPaymentServiceComponent).cancelPayment(testPayment.getId());
    }
}
