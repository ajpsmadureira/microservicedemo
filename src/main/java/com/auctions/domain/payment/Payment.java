package com.auctions.domain.payment;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Payment {

    private final Integer id;
    private final Integer auctionId;
    private final PaymentState state;
    private final String link;
    private final BigDecimal amount;
}
