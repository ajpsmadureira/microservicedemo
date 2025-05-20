package com.auctions.domain.auction;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Auction {

    private final Integer id;
    private final Instant startTime;
    private final Instant stopTime;
    private final Integer lotId;
    private final AuctionState state;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
}
