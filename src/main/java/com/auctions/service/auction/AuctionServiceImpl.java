package com.auctions.service.auction;

import com.auctions.domain.Auction;
import com.auctions.domain.User;
import com.auctions.service.auction.component.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final GetAuctionServiceComponent getAuctionSubService;
    private final CreateAuctionServiceComponent createAuctionSubService;
    private final UpdateAuctionServiceComponent updateAuctionSubService;
    private final StartAuctionServiceComponent startAuctionSubService;
    private final CancelAuctionServiceComponent cancelAuctionSubService;
    private final DeleteAuctionServiceComponent deleteAuctionSubService;

    @Override
    public List<Auction> getAllAuctions() {

        return getAuctionSubService.getAllAuctions();
    }

    @Override
    public Auction getAuctionById(Integer id) {

        return getAuctionSubService.getAuctionById(id);
    }

    @Override
    public Auction createAuction(Auction auction, User currentUser) {

        return createAuctionSubService.createAuction(auction, currentUser);
    }

    @Override
    public Auction updateAuctionDetails(Integer id, Auction auction, User currentUser) {

        return updateAuctionSubService.updateAuctionDetails(id, auction, currentUser);
    }

    @Override
    public void startAuction(Integer id) {

        startAuctionSubService.startAuction(id);
    }

    @Override
    public void cancelAuction(Integer id) {

        cancelAuctionSubService.cancelAuction(id);
    }

    @Override
    public void deleteAuction(Integer id) {

        deleteAuctionSubService.deleteAuction(id);
    }
} 