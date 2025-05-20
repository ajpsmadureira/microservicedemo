package com.auctions.service.payment.gateway;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
class AdyenPaymentGateway implements PaymentGateway {

    @Override
    public String createPaymentLink(BigDecimal amount) {

        // TODO

        return "https://adyen";
    }

    @Override
    public void cancelPayment(String link) {

        // TODO
    }
}
