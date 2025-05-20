package com.auctions.service.bid;

import com.auctions.domain.bid.Bid;
import com.auctions.domain.user.User;

import java.util.List;

public interface BidService {

    List<Bid> getAllBids();
    Bid getBidById(Integer id);
    Bid createBid(Bid bid, User currentUser);
    void acceptBid(Integer id);
    void cancelBid(Integer id);
}
