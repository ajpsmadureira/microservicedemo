package com.auctions.persistence.repository;

import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<LotEntity, Integer> {

    List<LotEntity> findByCreatedBy(UserEntity userEntity);
    List<LotEntity> findByLastModifiedBy(UserEntity userEntity);

    @Modifying
    @Query(value = "UPDATE bids SET state = 'REJECTED' WHERE state = 'CREATED' AND bids.lot = ?", nativeQuery = true)
    void rejectLotCreatedBids(Integer lotId);
} 