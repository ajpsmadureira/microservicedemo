package com.auctions.web.api.auction;

import lombok.Data;

import java.time.Instant;

@Data
public class AuctionUpdateRequest {

    private final Instant startTime;

    private final Instant stopTime;
} 