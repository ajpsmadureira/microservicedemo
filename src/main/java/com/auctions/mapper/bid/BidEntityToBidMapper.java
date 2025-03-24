package com.auctions.mapper.bid;

import com.auctions.domain.Bid;
import com.auctions.domain.Lot;
import com.auctions.domain.User;
import com.auctions.mapper.Mapper;
import com.auctions.mapper.lot.LotEntityToLotMapper;
import com.auctions.mapper.user.UserEntityToUserMapper;
import com.auctions.persistence.entity.BidEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BidEntityToBidMapper implements Mapper<BidEntity, Bid> {

    private final UserEntityToUserMapper userEntityToUserMapper;
    private final LotEntityToLotMapper lotEntityToLotMapper;

    @Override
    public Bid map(BidEntity bidEntity) {

        return Bid.builder()
                .id(bidEntity.getId())
                .amount(bidEntity.getAmount())
                .until(bidEntity.getUntil())
                .lotId(getLotId(bidEntity))
                .createdByUserId(getCreatedById(bidEntity))
                .lastModifiedByUserId(getLastModifiedById(bidEntity))
                .build();
    }

    private Integer getCreatedById(BidEntity bidEntity) {

        return Optional.ofNullable(bidEntity)
                .map(BidEntity::getCreatedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }

    private Integer getLastModifiedById(BidEntity bidEntity) {

        return Optional.ofNullable(bidEntity)
                .map(BidEntity::getLastModifiedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }

    private Integer getLotId(BidEntity bidEntity) {

        return Optional.ofNullable(bidEntity)
                .map(BidEntity::getLot)
                .map(lotEntityToLotMapper::map)
                .map(Lot::getId)
                .orElse(null);
    }
}
