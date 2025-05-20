package com.auctions.service.payment.component;

import com.auctions.domain.payment.Payment;
import com.auctions.domain.payment.PaymentState;
import com.auctions.domain.user.User;
import com.auctions.exception.BusinessException;
import com.auctions.mapper.payment.PaymentEntityToPaymentMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.PaymentEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.PaymentRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.payment.gateway.PaymentGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreatePaymentServiceComponent extends PaymentServiceComponent {

    public CreatePaymentServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, PaymentRepository paymentRepository, PaymentEntityToPaymentMapper paymentEntityToPaymentMapper, PaymentGateway paymentGateway) {
        super(userRepository, auctionRepository, paymentRepository, paymentEntityToPaymentMapper, paymentGateway);
    }

    @Transactional
    public Payment createPayment(Payment payment, User currentUser) {

        try {

            UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

            String link = paymentGateway.createPaymentLink(payment.getAmount());

            AuctionEntity auctionEntity = findAuctionByIdOrThrowException(payment.getAuctionId());

            PaymentEntity paymentEntity = new PaymentEntity();

            paymentEntity.setState(PaymentState.CREATED);
            paymentEntity.setAuction(auctionEntity);
            paymentEntity.setLink(link);
            paymentEntity.setAmount(payment.getAmount());
            paymentEntity.setCreatedBy(currentUserEntity);
            paymentEntity.setLastModifiedBy(currentUserEntity);

            PaymentEntity paymentEntitySaved = paymentRepository.save(paymentEntity);

            return paymentEntityToPaymentMapper.map(paymentEntitySaved);

        } catch (Exception e) {

            throw new BusinessException("Failed to create payment: " + e.getMessage());
        }
    }
}
