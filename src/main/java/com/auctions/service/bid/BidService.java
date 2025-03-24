package com.auctions.service.bid;

import com.auctions.domain.Bid;
import com.auctions.domain.User;

public interface BidService {

    Bid createBid(Bid bid, User currentUser);
    void deleteBid(Integer id);
}
