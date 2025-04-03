package com.auctions.service.auction;

import com.auctions.domain.Auction;
import com.auctions.domain.User;

import java.util.List;

public interface AuctionService {

    List<Auction> getAllAuctions();
    Auction getAuctionById(Integer id);
    Auction createAuction(Auction auction, User currentUser);
    Auction updateAuctionDetails(Integer id, Auction auction, User currentUser);
    void startAuction(Integer id);
    void cancelAuction(Integer id);
    void deleteAuction(Integer id);
}
