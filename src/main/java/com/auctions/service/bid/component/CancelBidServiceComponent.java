package com.auctions.service.bid.component;

import com.auctions.domain.bid.BidState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.time.Instant.now;

@Component
public class CancelBidServiceComponent extends BidServiceComponent {

    public CancelBidServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, BidRepository bidRepository, BidEntityToBidMapper bidEntityToBidMapper) {
        super(userRepository, auctionRepository, bidRepository, bidEntityToBidMapper);
    }

    @Transactional
    public void cancelBid(Integer id) {

        BidEntity bidEntity = findByIdOrThrowException(id);

        if (bidEntity.getState() == BidState.CANCELLED) {

            return;
        }

        if (bidEntity.getState() != BidState.CREATED) {

            throw new InvalidParameterException("Only bids in created state can be cancelled. Bid state is " + bidEntity.getState());
        }

        Optional.ofNullable(bidEntity.getUntil()).ifPresent(until -> {
            if (until.isBefore(now())) throw new InvalidParameterException("Bid has already stopped at: " + until);
        });

        try {

            bidEntity.setState(BidState.CANCELLED);

            bidRepository.save(bidEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to cancel bid: " + e.getMessage());
        }
    }
}
