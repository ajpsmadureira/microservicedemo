package com.auctions.service.payment;

import com.auctions.domain.payment.Payment;
import com.auctions.domain.user.User;

public interface PaymentService {

    Payment createPayment(Payment payment, User currentUser);
    Payment getPaymentById(Integer id);
    void cancelPayment(Integer id);
}
