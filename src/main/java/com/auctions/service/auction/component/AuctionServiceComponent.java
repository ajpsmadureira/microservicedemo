package com.auctions.service.auction.component;

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

@RequiredArgsConstructor
abstract class AuctionServiceComponent {

    final UserRepository userRepository;
    final AuctionRepository auctionRepository;
    final LotRepository lotRepository;
    final AuctionEntityToAuctionMapper auctionEntityToAuctionMapper;

    AuctionEntity findAuctionByIdOrThrowException(Integer id) {

        return auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find auction with id: " + id));
    }

    UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new BusinessException("Failed to find user with id: " + id));
    }

    LotEntity findLotByIdOrThrowException(Integer id) {

        return lotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find lot with id: " + id));
    }
}
