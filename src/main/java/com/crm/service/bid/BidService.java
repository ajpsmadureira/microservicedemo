package com.crm.service.bid;

import com.crm.domain.Bid;
import com.crm.domain.User;

public interface BidService {

    Bid createBid(Bid bid, User currentUser);
    void deleteBid(Integer id);
}
