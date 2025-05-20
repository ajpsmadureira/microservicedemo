package com.auctions.mapper.lot;

import com.auctions.domain.lot.Lot;
import com.auctions.domain.user.User;
import com.auctions.persistence.entity.LotEntity;
import com.auctions.mapper.Mapper;
import com.auctions.mapper.user.UserEntityToUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LotEntityToLotMapper implements Mapper<LotEntity, Lot> {

    private final UserEntityToUserMapper userEntityToUserMapper;

    @Override
    public Lot map(LotEntity lotEntity) {

        return Lot.builder()
                .id(lotEntity.getId())
                .name(lotEntity.getName())
                .surname(lotEntity.getSurname())
                .createdByUserId(getCreatedById(lotEntity))
                .lastModifiedByUserId(getLastModifiedById(lotEntity))
                .build();
    }

    private Integer getCreatedById(LotEntity lotEntity) {

        return Optional.ofNullable(lotEntity)
                .map(LotEntity::getCreatedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }

    private Integer getLastModifiedById(LotEntity lotEntity) {

        return Optional.ofNullable(lotEntity)
                .map(LotEntity::getLastModifiedBy)
                .map(userEntityToUserMapper::map)
                .map(User::getId)
                .orElse(null);
    }
}
