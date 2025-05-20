package com.auctions.mapper.payment;

import com.auctions.domain.payment.Payment;
import com.auctions.mapper.Mapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.PaymentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentEntityToPaymentMapper implements Mapper<PaymentEntity, Payment> {

    @Override
    public Payment map(PaymentEntity paymentEntity) {

        return Payment.builder()
                .id(paymentEntity.getId())
                .amount(paymentEntity.getAmount())
                .auctionId(getAuctionId(paymentEntity))
                .link(paymentEntity.getLink())
                .build();
    }

    private Integer getAuctionId(PaymentEntity paymentEntity) {

        return Optional.ofNullable(paymentEntity)
                .map(PaymentEntity::getAuction)
                .map(AuctionEntity::getId)
                .orElse(null);
    }
}
