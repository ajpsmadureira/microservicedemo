package com.auctions.web.api.auction;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Builder
@Data
public class AuctionResponse {

    private final Integer id;
    private final Instant startTime;
    private final Instant stopTime;
    private final Integer lotId;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
} 