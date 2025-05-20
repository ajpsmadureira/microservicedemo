package com.auctions.service.payment.component;

import com.auctions.domain.payment.PaymentState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.mapper.payment.PaymentEntityToPaymentMapper;
import com.auctions.persistence.entity.PaymentEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.PaymentRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.payment.gateway.PaymentGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelPaymentServiceComponent extends PaymentServiceComponent {

    public CancelPaymentServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, PaymentRepository paymentRepository, PaymentEntityToPaymentMapper paymentEntityToPaymentMapper, PaymentGateway paymentGateway) {
        super(userRepository, auctionRepository, paymentRepository, paymentEntityToPaymentMapper, paymentGateway);
    }

    @Transactional
    public void cancelPayment(Integer id) {

        PaymentEntity paymentEntity = findPaymentByIdOrThrowException(id);

        if (paymentEntity.getState() == PaymentState.CANCELLED) {

            return;
        }

        if (paymentEntity.getState() == PaymentState.DONE) {

            throw new InvalidParameterException("Payment has been done, thus cannot be cancelled.");
        }

        try {

            paymentGateway.cancelPayment(paymentEntity.getLink());

            paymentEntity.setState(PaymentState.CANCELLED);

            paymentRepository.save(paymentEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to cancel payment: " + e.getMessage());
        }
    }
}
