package com.crm.service;

import com.crm.domain.*;
import com.crm.exception.BusinessException;
import com.crm.mapper.bid.BidEntityToBidMapper;
import com.crm.persistence.entity.BidEntity;
import com.crm.persistence.entity.LotEntity;
import com.crm.persistence.entity.UserEntity;
import com.crm.persistence.repository.BidRepository;
import com.crm.persistence.repository.LotRepository;
import com.crm.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BidService {

    private final UserRepository userRepository;
    private final LotRepository lotRepository;
    private final BidRepository bidRepository;
    private final BidEntityToBidMapper bidEntityToBidMapper;

    @Transactional
    public Bid createBid(Bid bid, User currentUser) {

        try {

            UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

            LotEntity lotEntity = findLotByIdOrThrowException(bid.getLotId());

            if (lotEntity.getState() != LotState.AUCTIONED) {

                throw new RuntimeException("Lot is not being auctioned: {}" + lotEntity.getId());
            }

            BidEntity bidEntity = new BidEntity();

            bidEntity.setUntil(bid.getUntil());
            bidEntity.setAmount(bid.getAmount());
            bidEntity.setLot(lotEntity);
            bidEntity.setState(BidState.OPENED);
            bidEntity.setCreatedBy(currentUserEntity);
            bidEntity.setLastModifiedBy(currentUserEntity);

            BidEntity newBidEntitySaved = bidRepository.save(bidEntity);

            return bidEntityToBidMapper.map(newBidEntitySaved);

        } catch (Exception e) {

            throw new BusinessException("Failed to create bid: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteBid(Integer id) {

        try {

            bidRepository.deleteById(id);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete bid: " + e.getMessage());
        }
    }

    private UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find user with id: " + id));
    }

    private LotEntity findLotByIdOrThrowException(Integer id) {

        return lotRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find lot with id: " + id));
    }
} 