package com.auctions.service.bid;

import com.auctions.domain.bid.Bid;
import com.auctions.domain.user.User;
import com.auctions.service.bid.component.CancelBidServiceComponent;
import com.auctions.service.bid.component.CreateBidServiceComponent;
import com.auctions.service.bid.component.GetBidServiceComponent;
import com.auctions.service.bid.component.UpdateBidServiceComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final GetBidServiceComponent getBidServiceComponent;
    private final CreateBidServiceComponent createBidServiceComponent;
    private final CancelBidServiceComponent cancelBidServiceComponent;
    private final UpdateBidServiceComponent updateBidServiceComponent;

    @Override
    public List<Bid> getAllBids() {

        return getBidServiceComponent.getAllBids();
    }

    @Override
    public Bid getBidById(Integer id) {

        return getBidServiceComponent.getBidById(id);
    }

    @Override
    public Bid createBid(Bid bid, User currentUser) {

        return createBidServiceComponent.createBid(bid, currentUser);
    }

    @Override
    public void cancelBid(Integer id) {

        cancelBidServiceComponent.cancelBid(id);
    }

    @Override
    public int updateBidsStateToOutdated() {

        return updateBidServiceComponent.updateBidsStateToOutdated();
    }

    @Override
    public void acceptBid(Integer id) {

        updateBidServiceComponent.acceptBid(id);
    }
} 