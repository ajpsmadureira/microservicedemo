package com.auctions.service.auction.component;

import com.auctions.domain.AuctionState;
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
public class StartAuctionServiceComponent extends AuctionServiceComponent {

    public StartAuctionServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, LotRepository lotRepository, AuctionEntityToAuctionMapper auctionEntityToAuctionMapper) {
        super(userRepository, auctionRepository, lotRepository, auctionEntityToAuctionMapper);
    }

    @Transactional
    public void startAuction(Integer id) {

        AuctionEntity auctionEntity = findAuctionByIdOrThrowException(id);

        if (auctionEntity.getState() == AuctionState.ONGOING) {

            return;
        }

        if (auctionEntity.getState() != AuctionState.CREATED) {

            throw new InvalidParameterException("Auction is in wrong state to be started: " + auctionEntity.getState());
        }

        Optional.ofNullable(auctionEntity.getStopTime()).ifPresent(stopTime -> {
            if (stopTime.isBefore(now())) throw new InvalidParameterException("Auction has already stopped at: " + stopTime);
        });

        try {

            auctionEntity.setStartTime(now());

            auctionEntity.setState(AuctionState.ONGOING);

            // TODO: handle stop time

            auctionRepository.save(auctionEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to start auction: " + e.getMessage());
        }
    }
}
