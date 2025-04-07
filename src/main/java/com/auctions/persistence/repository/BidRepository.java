package com.auctions.persistence.repository;

import com.auctions.persistence.entity.BidEntity;
import com.auctions.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<BidEntity, Integer> {

    List<BidEntity> findByCreatedBy(UserEntity userEntity);
    List<BidEntity> findByLastModifiedBy(UserEntity userEntity);

    @Modifying
    @Query(value = "UPDATE bids SET state = 'OUTDATED' WHERE state = 'CREATED' AND until < now()", nativeQuery = true)
    int updateBidsStateToOutdated();
} 