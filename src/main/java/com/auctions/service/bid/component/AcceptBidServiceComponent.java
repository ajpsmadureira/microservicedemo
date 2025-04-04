package com.auctions.service.bid.component;

import com.auctions.domain.AuctionState;
import com.auctions.domain.BidState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.time.Instant.now;

@Component
public class AcceptBidServiceComponent extends BidServiceComponent {

    public AcceptBidServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, BidRepository bidRepository, BidEntityToBidMapper bidEntityToBidMapper) {
        super(userRepository, auctionRepository, bidRepository, bidEntityToBidMapper);
    }

    @Transactional
    public void acceptBid(Integer id) {

        BidEntity bidEntity = findByIdOrThrowException(id);

        if (bidEntity.getState() == BidState.ACCEPTED) {

            return;
        }

        if (bidEntity.getState() != BidState.CREATED) {

            throw new InvalidParameterException("Only bids in created state can be accepted. Bid state is " + bidEntity.getState());
        }

        if (bidEntity.getUntil().isBefore(now())) {

            throw new InvalidParameterException("Bid is outdated. Bid is until " + bidEntity.getUntil());
        }

        AuctionEntity auction = bidEntity.getAuction();

        if (auction.getState() != AuctionState.ONGOING) {

            throw new InvalidParameterException("Auction is not ongoing. Its state is " + auction.getState());
        }

        try {

            bidEntity.setState(BidState.ACCEPTED);

            auction.setState(AuctionState.CLOSED);

            auctionRepository.save(auction);

            auctionRepository.updateAuctionCreatedBidsState(BidState.REJECTED, auction.getId());

            bidRepository.save(bidEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to accept bid: " + e.getMessage());
        }
    }
}
