package com.auctions.web.api.bid;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class BidCreateRequest {

    @NotBlank(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Auction id is required")
    private Integer auctionId;

    private final Instant until;
} 