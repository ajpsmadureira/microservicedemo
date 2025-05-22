package com.auctions.task.payment;

import com.auctions.domain.payment.Payment;
import com.auctions.domain.payment.PaymentState;
import com.auctions.service.accounting.AccountingService;
import com.auctions.service.auction.AuctionService;
import com.auctions.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePaymentTask {

    private final AuctionService auctionService;
    private final PaymentService paymentService;
    private final AccountingService accountingService;

    @Scheduled(cron = "${task.payment-create.cron}")
    @Transactional
    public void createPayment() {

        auctionService.getAllAuctions().forEach(auction -> {

            Integer auctionId = auction.getId();

            List<Payment> listAuctionPayments = paymentService.getPaymentsByAuctionId(auctionId);

            boolean isThereAnyPaymentDone = listAuctionPayments.stream().anyMatch(payment -> payment.getState() == PaymentState.DONE);

            boolean isThereAnyPaymentCreated = listAuctionPayments.stream().anyMatch(payment -> payment.getState() == PaymentState.CREATED);

            if (!isThereAnyPaymentDone && !isThereAnyPaymentCreated) {

                BigDecimal amount = accountingService.getAuctionCost(auctionId);

                Payment payment = Payment
                        .builder()
                        .amount(amount)
                        .auctionId(auctionId)
                        .build();

                Payment createdPayment = paymentService.createPayment(payment, null);

                log.info("Created payment id {} for auction id {}", createdPayment.getId(), auctionId);
            }
        });
    }
}