package com.auctions.service.auction.component;

import com.auctions.domain.Auction;
import com.auctions.domain.AuctionState;
import com.auctions.domain.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static java.time.Instant.now;

@Component
public class UpdateAuctionServiceComponent extends AuctionServiceComponent {

    public UpdateAuctionServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, LotRepository lotRepository, AuctionEntityToAuctionMapper auctionEntityToAuctionMapper) {
        super(userRepository, auctionRepository, lotRepository, auctionEntityToAuctionMapper);
    }

    @Transactional
    public Auction updateAuctionDetails(Integer id, Auction auction, User currentUser) {

        AuctionEntity auctionEntity = findAuctionByIdOrThrowException(id);

        updateStartTime(auction, auctionEntity);

        updateStopTime(auction, auctionEntity);

        validateStartAndStopTimes(auctionEntity);

        updateSetLastModifiedBy(auctionEntity, currentUser);

        return saveUpdatedAuctionEntity(auctionEntity);
    }

    private void updateStartTime(Auction auction, AuctionEntity auctionEntity) {

        updateAuctionTime(
                auction.getStartTime(),
                auctionEntity.getStartTime(),
                EnumSet.of(AuctionState.CREATED),
                auctionEntity.getState(),
                "start",
                auctionEntity::setStartTime
        );
    }

    private void updateStopTime(Auction auction, AuctionEntity auctionEntity) {

        updateAuctionTime(
                auction.getStopTime(),
                auctionEntity.getStopTime(),
                EnumSet.of(AuctionState.CREATED, AuctionState.ONGOING),
                auctionEntity.getState(),
                "stop",
                auctionEntity::setStopTime
        );
    }

    private void updateAuctionTime(
            Instant newTime,
            Instant currentTime,
            Set<AuctionState> allowedStates,
            AuctionState currentState,
            String label,
            Consumer<Instant> setter
    ) {
        Optional.ofNullable(newTime)
                .filter(t -> !t.equals(currentTime))
                .ifPresent(t -> {
                    if (t.isBefore(now())) {
                        throw new InvalidParameterException("Auction desired " + label + " time is in the past: " + t);
                    }
                    if (!allowedStates.contains(currentState)) {
                        throw new InvalidParameterException("Auction " + label + " time cannot be updated because state is: " + currentState);
                    }
                    setter.accept(t);
                });
    }

    private void validateStartAndStopTimes(AuctionEntity auctionEntity) {

        if (Objects.nonNull(auctionEntity.getStartTime()) && Objects.nonNull(auctionEntity.getStopTime())) {
            if (!auctionEntity.getStartTime().isBefore(auctionEntity.getStopTime())) {
                throw new InvalidParameterException("Auction start time needs to be before stop time.");
            }
        }
    }

    private void updateSetLastModifiedBy(AuctionEntity auctionEntity, User currentUser) {

        UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        auctionEntity.setLastModifiedBy(currentUserEntity);
    }

    private Auction saveUpdatedAuctionEntity(AuctionEntity auctionEntity) {

        try {

            AuctionEntity updatedAuctionEntity = auctionRepository.save(auctionEntity);

            return auctionEntityToAuctionMapper.map(updatedAuctionEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to update auction details: " + e.getMessage());
        }
    }
}
