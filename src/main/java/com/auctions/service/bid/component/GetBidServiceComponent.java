package com.auctions.service.bid.component;

import com.auctions.domain.bid.Bid;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetBidServiceComponent extends BidServiceComponent {

    public GetBidServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, BidRepository bidRepository, BidEntityToBidMapper bidEntityToBidMapper) {
        super(userRepository, auctionRepository, bidRepository, bidEntityToBidMapper);
    }

    public List<Bid> getAllBids() {

        return bidRepository
                .findAll()
                .stream()
                .map(bidEntityToBidMapper::map)
                .toList();
    }

    public Bid getBidById(Integer id) {

        return bidRepository.findById(id)
                .map(bidEntityToBidMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found with id: " + id));
    }
}
