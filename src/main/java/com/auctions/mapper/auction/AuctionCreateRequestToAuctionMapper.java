package com.auctions.mapper.auction;

import com.auctions.domain.auction.Auction;
import com.auctions.mapper.Mapper;
import com.auctions.web.api.auction.AuctionCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class AuctionCreateRequestToAuctionMapper implements Mapper<AuctionCreateRequest, Auction> {

    @Override
    public Auction map(AuctionCreateRequest auctionCreateRequest) {

        return Auction.builder()
                .lotId(auctionCreateRequest.getLotId())
                .startTime(auctionCreateRequest.getStartTime())
                .stopTime(auctionCreateRequest.getStopTime())
                .build();
    }
}
