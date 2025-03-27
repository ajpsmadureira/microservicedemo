package com.auctions.service.auction;

import com.auctions.domain.Auction;
import com.auctions.domain.AuctionState;
import com.auctions.domain.User;
import com.auctions.exception.BusinessException;
import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.auction.AuctionEntityToAuctionMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.LotRepository;
import com.auctions.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionServiceImpl implements AuctionService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final LotRepository lotRepository;
    private final AuctionEntityToAuctionMapper auctionEntityToAuctionMapper;

    public List<Auction> getAllAuctions() {

        return auctionRepository
                .findAll()
                .stream()
                .map(auctionEntityToAuctionMapper::map)
                .toList();
    }

    public Auction getAuctionById(Integer id) {

        return auctionRepository.findById(id)
                .map(auctionEntityToAuctionMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + id));
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

            throw new BusinessException("Failed to create Auction: " + e.getMessage());
        }
    }

    @Transactional
    public Auction updateAuctionDetails(Integer id, Auction auction, User currentUser) {

        AuctionEntity auctionEntity = findAuctionByIdOrThrowException(id);

        Optional.ofNullable(auction.getStartTime()).ifPresent(auctionEntity::setStartTime);

        Optional.ofNullable(auction.getStopTime()).ifPresent(auctionEntity::setStopTime);

        UserEntity currentUserEntity = findUserByIdOrThrowException(currentUser.getId());

        auctionEntity.setLastModifiedBy(currentUserEntity);

        try {
            
            AuctionEntity updatedAuctionEntity = auctionRepository.save(auctionEntity);

            return auctionEntityToAuctionMapper.map(updatedAuctionEntity);

        } catch (Exception e) {

            throw new BusinessException("Failed to update auction details: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteAuction(Integer id) {

        AuctionEntity auctionEntity;

        try {

            auctionEntity = findAuctionByIdOrThrowException(id);

        } catch(ResourceNotFoundException e) {

            return;
        }

        try {

            auctionRepository.deleteById(id);

        } catch (Exception e) {

            throw new BusinessException("Failed to delete auction: " + e.getMessage());
        }
    }

    private UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find user with id: " + id));
    }

    private LotEntity findLotByIdOrThrowException(Integer id) {

        return lotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find lot with id: " + id));
    }

    private AuctionEntity findAuctionByIdOrThrowException(Integer id) {

        return auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find auction with id: " + id));
    }
} 