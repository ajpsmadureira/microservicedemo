package com.auctions.persistence.repository;

import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionRepository extends JpaRepository<AuctionEntity, Integer> {

    List<AuctionEntity> findByCreatedBy(UserEntity userEntity);
    List<AuctionEntity> findByLastModifiedBy(UserEntity userEntity);

    @Modifying
    @Query(value = "UPDATE bids SET state = 'REJECTED' WHERE state = 'CREATED' AND bids.auction = ?", nativeQuery = true)
    void rejectAuctionCreatedBids(Integer auctionId);
} 