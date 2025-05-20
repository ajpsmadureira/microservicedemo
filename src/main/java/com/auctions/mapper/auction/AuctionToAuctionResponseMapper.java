package com.auctions.mapper.auction;

import com.auctions.domain.auction.Auction;
import com.auctions.mapper.Mapper;
import com.auctions.web.api.auction.AuctionResponse;
import org.springframework.stereotype.Component;

@Component
public class AuctionToAuctionResponseMapper implements Mapper<Auction, AuctionResponse> {

    @Override
    public AuctionResponse map(Auction auction) {

        return AuctionResponse.builder()
                .id(auction.getId())
                .startTime(auction.getStartTime())
                .stopTime(auction.getStopTime())
                .lotId(auction.getLotId())
                .createdByUserId(auction.getCreatedByUserId())
                .lastModifiedByUserId(auction.getLastModifiedByUserId())
                .build();
    }
}
