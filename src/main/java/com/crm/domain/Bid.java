package com.crm.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Bid {

    private final Integer id;
    private final BigDecimal amount;
    private final Instant until;
    private final Integer lotId;
    private final BidState state;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
}
