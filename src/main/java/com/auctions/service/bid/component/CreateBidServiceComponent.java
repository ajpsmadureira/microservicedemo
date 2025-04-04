package com.auctions.service.bid.component;

import com.auctions.domain.AuctionState;
import com.auctions.domain.Bid;
import com.auctions.domain.BidState;
import com.auctions.domain.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CreateBidServiceComponent extends BidServiceComponent {

    public CreateBidServiceComponent(UserRepository userRepository, AuctionRepository auctionRepository, BidRepository bidRepository, BidEntityToBidMapper bidEntityToBidMapper) {
        super(userRepository, auctionRepository, bidRepository, bidEntityToBidMapper);
    }

    @Transactional
    public Bid createBid(Bid bid, User currentUser) {

        UserEntity currentUserEntity;

        try {
            currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        } catch(ResourceNotFoundException e) {

            throw new BusinessException("Failed to create bid: " + e.getMessage());
        }

        AuctionEntity auctionEntity = findAuctionByIdOrThrowException(bid.getAuctionId());

        if (auctionEntity.getState() != AuctionState.ONGOING) {

            throw new InvalidParameterException("Auction is not ongoing: " + auctionEntity.getId());
        }

        BidEntity bidEntity = new BidEntity();

        bidEntity.setUntil(bid.getUntil());
        bidEntity.setAmount(bid.getAmount());
        bidEntity.setAuction(auctionEntity);
        bidEntity.setState(BidState.CREATED);
        bidEntity.setCreatedBy(currentUserEntity);
        bidEntity.setLastModifiedBy(currentUserEntity);

        try {

            BidEntity newBidEntitySaved = bidRepository.save(bidEntity);

            return bidEntityToBidMapper.map(newBidEntitySaved);

        } catch (Exception e) {

            throw new BusinessException("Failed to create bid: " + e.getMessage());
        }
    }
}
