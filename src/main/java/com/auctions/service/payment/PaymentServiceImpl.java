package com.auctions.service.payment;

import com.auctions.domain.payment.Payment;
import com.auctions.domain.user.User;
import com.auctions.service.payment.component.CancelPaymentServiceComponent;
import com.auctions.service.payment.component.CreatePaymentServiceComponent;
import com.auctions.service.payment.component.GetPaymentServiceComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final CreatePaymentServiceComponent createPaymentServiceComponent;
    private final GetPaymentServiceComponent getPaymentServiceComponent;
    private final CancelPaymentServiceComponent cancelPaymentServiceComponent;

    @Override
    public Payment createPayment(Payment payment, User currentUser) {

        return createPaymentServiceComponent.createPayment(payment, currentUser);
    }

    @Override
    public Payment getPaymentById(Integer id) {

        return getPaymentServiceComponent.getPaymentById(id);
    }

    @Override
    public void cancelPayment(Integer id) {

        cancelPaymentServiceComponent.cancelPayment(id);
    }
}
