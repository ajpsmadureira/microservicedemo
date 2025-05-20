package com.auctions.mapper.lot;

import com.auctions.domain.lot.Lot;
import com.auctions.mapper.Mapper;
import com.auctions.web.api.lot.LotUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class LotUpdateRequestToLotMapper implements Mapper<LotUpdateRequest, Lot> {

    @Override
    public Lot map(LotUpdateRequest lotUpdateRequest) {

        return Lot.builder()
                .name(lotUpdateRequest.getName())
                .surname(lotUpdateRequest.getSurname())
                .build();
    }
}
