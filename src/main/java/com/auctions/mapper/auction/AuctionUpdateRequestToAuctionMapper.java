package com.auctions.mapper.auction;

import com.auctions.domain.auction.Auction;
import com.auctions.mapper.Mapper;
import com.auctions.web.api.auction.AuctionUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class AuctionUpdateRequestToAuctionMapper implements Mapper<AuctionUpdateRequest, Auction> {

    @Override
    public Auction map(AuctionUpdateRequest auctionUpdateRequest) {

        return Auction.builder()
                .startTime(auctionUpdateRequest.getStartTime())
                .stopTime(auctionUpdateRequest.getStopTime())
                .build();
    }
}
