package com.auctions.mapper.lot;

import com.auctions.domain.Lot;
import com.auctions.web.api.lot.LotResponse;
import com.auctions.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class LotToLotResponseMapper implements Mapper<Lot, LotResponse> {

    @Override
    public LotResponse map(Lot lot) {

        return LotResponse.builder()
                .id(lot.getId())
                .name(lot.getName())
                .surname(lot.getSurname())
                .createdByUserId(lot.getCreatedByUserId())
                .lastModifiedByUserId(lot.getLastModifiedByUserId())
                .build();
    }
}
