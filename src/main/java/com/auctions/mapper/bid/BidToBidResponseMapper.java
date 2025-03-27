package com.auctions.mapper.bid;

import com.auctions.domain.Bid;
import com.auctions.mapper.Mapper;
import com.auctions.web.api.bid.BidResponse;
import org.springframework.stereotype.Component;

@Component
public class BidToBidResponseMapper implements Mapper<Bid, BidResponse> {

    @Override
    public BidResponse map(Bid bid) {

        return BidResponse.builder()
                .id(bid.getId())
                .amount(bid.getAmount())
                .auctionId(bid.getAuctionId())
                .until(bid.getUntil())
                .createdByUserId(bid.getCreatedByUserId())
                .lastModifiedByUserId(bid.getLastModifiedByUserId())
                .build();
    }
}
