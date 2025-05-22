package com.auctions.service.accounting;

import java.math.BigDecimal;

public interface AccountingService {

    BigDecimal getAuctionCost(Integer auctionId);
}
