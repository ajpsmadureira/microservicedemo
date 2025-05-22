package com.auctions.task.bid;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.lot.Lot;
import com.auctions.domain.payment.Payment;
import com.auctions.domain.payment.PaymentState;
import com.auctions.domain.user.User;
import com.auctions.service.accounting.AccountingService;
import com.auctions.service.auction.AuctionService;
import com.auctions.service.payment.PaymentService;
import com.auctions.task.payment.CreatePaymentTask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.auctions.util.TestDataFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreatePaymentTaskTest {

    private static final BigDecimal BID_AMOUNT = BigDecimal.valueOf(1);

    @Mock
    private AuctionService auctionService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AccountingService accountingService;

    @InjectMocks
    private CreatePaymentTask createPaymentTask;

    @Test
    void createPayment_noPaymentCreated() {

        User user = createTestUser();

        Lot lot = createTestLot(user);

        Auction auction = createTestAuction(user, lot);

        Integer auctionId = auction.getId();

        when(auctionService.getAllAuctions()).thenReturn(List.of(auction));

        when(paymentService.getPaymentsByAuctionId(auctionId)).thenReturn(List.of());

        Payment payment = createTestPayment(auction);

        when(paymentService.createPayment(any(), any())).thenReturn(payment);

        when(accountingService.getAuctionCost(auctionId)).thenReturn(BID_AMOUNT);

        createPaymentTask.createPayment();

        verify(paymentService).getPaymentsByAuctionId(eq(auctionId));

        verify(accountingService).getAuctionCost(eq(auctionId));

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentService).createPayment(paymentCaptor.capture(), eq(null));
        Payment paymentCaptured = paymentCaptor.getValue();
        assertEquals(BID_AMOUNT, paymentCaptured.getAmount());
        assertEquals(auctionId, paymentCaptured.getAuctionId());
    }

    @Test
    void createPayment_paymentDone() {

        User user = createTestUser();

        Lot lot = createTestLot(user);

        Auction auction = createTestAuction(user, lot);

        Integer auctionId = auction.getId();

        when(auctionService.getAllAuctions()).thenReturn(List.of(auction));

        Payment payment = createTestPayment(auction)
                .toBuilder()
                .state(PaymentState.DONE)
                .build();

        when(paymentService.getPaymentsByAuctionId(auctionId)).thenReturn(List.of(payment));

        createPaymentTask.createPayment();

        verify(accountingService, never()).getAuctionCost(any());

        verify(paymentService, never()).createPayment(any(), any());
    }

    @Test
    void createPayment_paymentCreated() {

        User user = createTestUser();

        Lot lot = createTestLot(user);

        Auction auction = createTestAuction(user, lot);

        Integer auctionId = auction.getId();

        when(auctionService.getAllAuctions()).thenReturn(List.of(auction));

        Payment payment = createTestPayment(auction)
                .toBuilder()
                .state(PaymentState.CREATED)
                .build();

        when(paymentService.getPaymentsByAuctionId(auctionId)).thenReturn(List.of(payment));

        createPaymentTask.createPayment();

        verify(accountingService, never()).getAuctionCost(any());

        verify(paymentService, never()).createPayment(any(), any());
    }
}
