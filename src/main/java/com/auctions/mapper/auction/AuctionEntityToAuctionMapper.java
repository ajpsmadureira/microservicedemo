package com.auctions.mapper.auction;

import com.auctions.domain.Auction;
import com.auctions.domain.User;
import com.auctions.mapper.Mapper;
import com.auctions.mapper.user.UserEntityToUserMapper;
import com.auctions.persistence.entity.AuctionEntity;
import com.auctions.persistence.entity.LotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuctionEntityToAuctionMapper implements Mapper<AuctionEntity, Auction> {

    private final UserEntityToUserMapper userEntityToUserMapper;

    @Override
    public Auction map(AuctionEntity auctionEntity) {

        return Auction.builder()
                .id(auctionEntity.getId())
                .startTime(auctionEntity.getStartTime())
                .stopTime(auctionEntity.getStopTime())
                .lotId(getLotId(auctionEntity))
                .createdByUserId(getCreatedById(auctionEntity))
                .lastModifiedByUserId(getLastModifiedById(auctionEntity))
                .build();
    }

    private Integer getCreatedById(AuctionEntity auctionEntity) {

        return Optional.ofNullable(auctionEntity)
                .map(AuctionEntity::getCreatedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }

    private Integer getLastModifiedById(AuctionEntity auctionEntity) {

        return Optional.ofNullable(auctionEntity)
                .map(AuctionEntity::getLastModifiedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }

    private Integer getLotId(AuctionEntity auctionEntity) {

        return Optional.ofNullable(auctionEntity)
                .map(AuctionEntity::getLot)
                .map(LotEntity::getId)
                .orElse(null);
    }
}
