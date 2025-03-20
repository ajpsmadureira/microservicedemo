package com.crm.mapper.lot;

import com.crm.domain.Bid;
import com.crm.mapper.Mapper;
import com.crm.web.api.bid.BidCreateRequest;
import org.springframework.stereotype.Component;

@Component
public class BidCreateRequestToBidMapper implements Mapper<BidCreateRequest, Bid> {

    @Override
    public Bid map(BidCreateRequest bidCreateRequest) {

        return Bid.builder()
                .lotId(bidCreateRequest.getLotId())
                .amount(bidCreateRequest.getAmount())
                .until(bidCreateRequest.getUntil())
                .build();
    }
}
