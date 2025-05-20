package com.auctions.service.auction.component;

import com.auctions.domain.auction.Auction;
import com.auctions.domain.auction.AuctionState;
import com.auctions.domain.user.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateAuctionServiceComponent extends AuctionServiceComponent {

    public CreateAuctionServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, LotRepository lotRepository, AuctionEntityToAuctionMapper auctionEntityToAuctionMapper) {
        super(userRepository, auctionRepository, lotRepository, auctionEntityToAuctionMapper);
    }

    @Transactional
    public Auction createAuction(Auction auction, User currentUser) {

        UserEntity currentUserEntity;

        try {
            currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        } catch(ResourceNotFoundException e) {

            throw new BusinessException("Failed to create bid: " + e.getMessage());
        }

        LotEntity lotEntity = findLotByIdOrThrowException(auction.getLotId());

        AuctionEntity auctionEntity = new AuctionEntity();

        auctionEntity.setStartTime(auction.getStartTime());
        auctionEntity.setStopTime(auction.getStopTime());
        auctionEntity.setState(AuctionState.CREATED);
        auctionEntity.setLot(lotEntity);
        auctionEntity.setCreatedBy(currentUserEntity);
        auctionEntity.setLastModifiedBy(currentUserEntity);

        try {

            AuctionEntity auctionEntitySaved = auctionRepository.save(auctionEntity);

            return auctionEntityToAuctionMapper.map(auctionEntitySaved);

        } catch (Exception e) {

            throw new BusinessException("Failed to create auction: " + e.getMessage());
        }
    }
}
