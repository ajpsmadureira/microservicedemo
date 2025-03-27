package com.auctions.mapper.bid;

import com.auctions.domain.Bid;
import com.auctions.mapper.Mapper;
import com.auctions.web.api.bid.BidCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class BidCreateRequestToBidMapper implements Mapper<BidCreateRequest, Bid> {

    @Override
    public Bid map(BidCreateRequest bidCreateRequest) {

        return Bid.builder()
                .auctionId(bidCreateRequest.getAuctionId())
                .amount(bidCreateRequest.getAmount())
                .until(bidCreateRequest.getUntil())
                .build();
    }
}
