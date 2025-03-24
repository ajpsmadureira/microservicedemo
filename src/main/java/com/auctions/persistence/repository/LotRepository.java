package com.auctions.persistence.repository;

import com.auctions.persistence.entity.LotEntity;
import com.auctions.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LotRepository extends JpaRepository<LotEntity, Integer> {

    List<LotEntity> findByCreatedBy(UserEntity userEntity);
    List<LotEntity> findByLastModifiedBy(UserEntity userEntity);
} 