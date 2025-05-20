package com.auctions.service.auction;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.user.User;
import com.auctions.service.auction.component.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final GetAuctionServiceComponent getAuctionServiceComponent;
    private final CreateAuctionServiceComponent createAuctionServiceComponent;
    private final UpdateAuctionServiceComponent updateAuctionServiceComponent;
    private final StartAuctionServiceComponent startAuctionServiceComponent;
    private final CancelAuctionServiceComponent cancelAuctionServiceComponent;
    private final DeleteAuctionServiceComponent deleteAuctionServiceComponent;

    @Override
    public List<Auction> getAllAuctions() {

        return getAuctionServiceComponent.getAllAuctions();
    }

    @Override
    public Auction getAuctionById(Integer id) {

        return getAuctionServiceComponent.getAuctionById(id);
    }

    @Override
    public Auction createAuction(Auction auction, User currentUser) {

        return createAuctionServiceComponent.createAuction(auction, currentUser);
    }

    @Override
    public Auction updateAuctionDetails(Integer id, Auction auction, User currentUser) {

        return updateAuctionServiceComponent.updateAuctionDetails(id, auction, currentUser);
    }

    @Override
    public void startAuction(Integer id) {

        startAuctionServiceComponent.startAuction(id);
    }

    @Override
    public void cancelAuction(Integer id) {

        cancelAuctionServiceComponent.cancelAuction(id);
    }

    @Override
    public void deleteAuction(Integer id) {

        deleteAuctionServiceComponent.deleteAuction(id);
    }
} 