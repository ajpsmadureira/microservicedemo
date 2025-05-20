package com.auctions.service.payment;

import com.auctions.domain.payment.Payment;
import com.auctions.domain.user.User;
import com.auctions.service.payment.component.CreatePaymentServiceComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CreatePaymentServiceComponent createPaymentServiceComponent;

    @Override
    public Payment createPayment(Payment payment, User currentUser) {

        return createPaymentServiceComponent.createPayment(payment, currentUser);
    }
}
