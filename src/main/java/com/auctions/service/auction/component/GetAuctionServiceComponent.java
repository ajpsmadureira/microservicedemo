package com.auctions.service.auction.component;

import com.auctions.domain.auction.Auction;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GetAuctionServiceComponent extends AuctionServiceComponent {

    public GetAuctionServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, LotRepository lotRepository, AuctionEntityToAuctionMapper auctionEntityToAuctionMapper) {
        super(userRepository, auctionRepository, lotRepository, auctionEntityToAuctionMapper);
    }

    public List<Auction> getAllAuctions() {

        return auctionRepository
                .findAll()
                .stream()
                .map(auctionEntityToAuctionMapper::map)
                .toList();
    }

    public Auction getAuctionById(Integer id) {

        return auctionRepository.findById(id)
                .map(auctionEntityToAuctionMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + id));
    }
}
