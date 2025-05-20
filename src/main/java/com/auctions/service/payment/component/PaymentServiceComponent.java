package com.auctions.service.payment.component;

import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.payment.PaymentEntityToPaymentMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.PaymentEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.PaymentRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.payment.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class PaymentServiceComponent {

    final UserRepository userRepository;
    final AuctionRepository auctionRepository;
    final PaymentRepository paymentRepository;
    final PaymentEntityToPaymentMapper paymentEntityToPaymentMapper;
    final PaymentGateway paymentGateway;

    UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find user with id: " + id));
    }

    AuctionEntity findAuctionByIdOrThrowException(Integer id) {

        return auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find auction with id: " + id));
    }

    PaymentEntity findPaymentByIdOrThrowException(Integer id) {

        return paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find payment with id: " + id));
    }
}
