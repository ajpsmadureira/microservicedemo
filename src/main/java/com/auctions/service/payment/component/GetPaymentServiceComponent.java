package com.auctions.service.payment.component;

import com.auctions.domain.payment.Payment;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.payment.PaymentEntityToPaymentMapper;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.PaymentRepository;
import com.auctions.persistence.repository.UserRepository;
import com.auctions.service.payment.gateway.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetPaymentServiceComponent extends PaymentServiceComponent {

    public GetPaymentServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, PaymentRepository paymentRepository, PaymentEntityToPaymentMapper paymentEntityToPaymentMapper, PaymentGateway paymentGateway) {
        super(userRepository, auctionRepository, paymentRepository, paymentEntityToPaymentMapper, paymentGateway);
    }

    public Payment getPaymentById(Integer id) {

        return paymentRepository.findById(id)
                .map(paymentEntityToPaymentMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    public List<Payment> getPaymentsByAuctionId(Integer id) {

        return paymentRepository.findByAuctionId(id)
                .stream()
                .map(paymentEntityToPaymentMapper::map)
                .toList();
    }
}
