package com.auctions.service.accounting;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class AccountingServiceImpl implements AccountingService {

    @Override
    public BigDecimal getAuctionCost(Integer auctionId) {

        // TODO

        return BigDecimal.valueOf(10);
    }
}
