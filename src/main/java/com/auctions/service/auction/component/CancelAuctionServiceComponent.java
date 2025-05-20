package com.auctions.service.auction.component;

import com.auctions.domain.auction.AuctionState;
import com.auctions.domain.bid.BidState;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static java.time.Instant.now;

@Component
public class CancelAuctionServiceComponent extends AuctionServiceComponent {

    public CancelAuctionServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, LotRepository lotRepository, AuctionEntityToAuctionMapper auctionEntityToAuctionMapper) {
        super(userRepository, auctionRepository, lotRepository, auctionEntityToAuctionMapper);
    }

    @Transactional
    public void cancelAuction(Integer id) {

        AuctionEntity auctionEntity = findAuctionByIdOrThrowException(id);

        if (auctionEntity.getState() == AuctionState.CANCELLED) {

            return;
        }

        if (auctionEntity.getState() == AuctionState.CLOSED) {

            throw new InvalidParameterException("Auction has been closed, thus cannot be cancelled.");
        }

        Optional.ofNullable(auctionEntity.getStopTime()).ifPresent(stopTime -> {
            if (stopTime.isBefore(now())) throw new InvalidParameterException("Auction has already stopped at: " + stopTime);
        });

        try {

            auctionEntity.setState(AuctionState.CANCELLED);

            auctionRepository.updateAuctionCreatedBidsState(BidState.CANCELLED, auctionEntity.getId());

            auctionRepository.save(auctionEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to cancel auction: " + e.getMessage());
        }
    }
}
