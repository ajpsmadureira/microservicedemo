package com.auctions.web.api.auction;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Instant;

@Data
public class AuctionCreateRequest {

    @NotBlank(message = "Lot id is required")
    private Integer lotId;

    private final Instant startTime;

    private final Instant stopTime;
} 