package com.auctions.web.api.bid;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
public class BidResponse {

    private final Integer id;
    private final BigDecimal amount;
    private final Instant until;
    private final Integer lotId;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
} 