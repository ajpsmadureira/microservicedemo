package com.auctions.service.bid;

import com.auctions.domain.*;
import com.auctions.exception.BusinessException;
import com.auctions.exception.InvalidParameterException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.time.Instant.now;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {

    private final UserRepository userRepository;
    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final BidEntityToBidMapper bidEntityToBidMapper;

    @Transactional
    public Bid createBid(Bid bid, User currentUser) {

        UserEntity currentUserEntity;

        try {
            currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        } catch(ResourceNotFoundException e) {

            throw new BusinessException("Failed to create bid: " + e.getMessage());
        }

        LotEntity lotEntity = findLotByIdOrThrowException(bid.getLotId());

        if (lotEntity.getState() != LotState.AUCTIONED) {

            throw new InvalidParameterException("Lot is not being auctioned: " + lotEntity.getId());
        }

        BidEntity bidEntity = new BidEntity();

        bidEntity.setUntil(bid.getUntil());
        bidEntity.setAmount(bid.getAmount());
        bidEntity.setLot(lotEntity);
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

    @Transactional
    public void deleteBid(Integer id) {

        BidEntity bidEntity = findByIdOrThrowException(id);

        if (bidEntity.getState() == BidState.ACCEPTED) {

            throw new InvalidParameterException("An accepted bid cannot be deleted: " + bidEntity.getId());
        }

        try {

            bidRepository.deleteById(id);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete bid: " + e.getMessage());
        }
    }

    @Transactional
    public void cancelBid(Integer id) {

        BidEntity bidEntity = findByIdOrThrowException(id);

        if (bidEntity.getState() != BidState.CREATED) {

            throw new InvalidParameterException("Only bids in created state can be cancelled. Bid state is " + bidEntity.getState());
        }

        try {

            bidEntity.setState(BidState.CANCELLED);

            bidRepository.save(bidEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to cancel bid: " + e.getMessage());
        }
    }

    @Transactional
    public void acceptBid(Integer id) {

        BidEntity bidEntity = findByIdOrThrowException(id);

        if (bidEntity.getState() != BidState.CREATED) {

            throw new InvalidParameterException("Only bids in created state can be accepted. Bid state is " + bidEntity.getState());
        }

        if (bidEntity.getUntil().isBefore(now())) {

            throw new InvalidParameterException("Bid is outdated. Bid is until " + bidEntity.getUntil());
        }

        try {

            bidEntity.setState(BidState.ACCEPTED);

            bidRepository.save(bidEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to accept bid: " + e.getMessage());
        }
    }

    private UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find user with id: " + id));
    }

    private LotEntity findLotByIdOrThrowException(Integer id) {

        return lotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find lot with id: " + id));
    }

    private BidEntity findByIdOrThrowException(Integer id) {

        return bidRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find bid with id: " + id));
    }
} 