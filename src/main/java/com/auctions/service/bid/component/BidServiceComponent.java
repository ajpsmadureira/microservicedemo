package com.auctions.service.bid.component;

import com.auctions.exception.ResourceNotFoundException;
import com.auctions.mapper.bid.BidEntityToBidMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.persistence.repository.AuctionRepository;
import com.auctions.persistence.repository.BidRepository;
import com.auctions.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class BidServiceComponent {

    final UserRepository userRepository;
    final AuctionRepository auctionRepository;
    final BidRepository bidRepository;
    final BidEntityToBidMapper bidEntityToBidMapper;

    UserEntity findUserByIdOrThrowException(Integer id) {

        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find user with id: " + id));
    }

    AuctionEntity findAuctionByIdOrThrowException(Integer id) {

        return auctionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find auction with id: " + id));
    }

    BidEntity findByIdOrThrowException(Integer id) {

        return bidRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Failed to find bid with id: " + id));
    }
}
