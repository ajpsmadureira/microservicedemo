package com.auctions.service.bid;

import com.auctions.domain.bid.Bid;
import com.auctions.domain.user.User;
import com.auctions.service.bid.component.AcceptBidServiceComponent;
import com.auctions.service.bid.component.CancelBidServiceComponent;
import com.auctions.service.bid.component.CreateBidServiceComponent;
import com.auctions.service.bid.component.GetBidServiceComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final GetBidServiceComponent getBidServiceComponent;
    private final CreateBidServiceComponent createBidServiceComponent;
    private final CancelBidServiceComponent cancelBidServiceComponent;
    private final AcceptBidServiceComponent acceptBidServiceComponent;

    @Override
    public List<Bid> getAllBids() {

        return getBidServiceComponent.getAllBids();
    }

    @Override
    public Bid getBidById(Integer id) {

        return getBidServiceComponent.getBidById(id);
    }

    @Transactional
    public Bid createBid(Bid bid, User currentUser) {

        return createBidServiceComponent.createBid(bid, currentUser);
    }

    @Transactional
    public void cancelBid(Integer id) {

        cancelBidServiceComponent.cancelBid(id);
    }

    @Transactional
    public void acceptBid(Integer id) {

        acceptBidServiceComponent.acceptBid(id);
    }
} 