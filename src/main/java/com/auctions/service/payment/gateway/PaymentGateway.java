package com.auctions.service.payment.gateway;

import java.math.BigDecimal;

public interface PaymentGateway {

    String createPaymentLink(BigDecimal amount);
    void cancelPayment(String link);
}
