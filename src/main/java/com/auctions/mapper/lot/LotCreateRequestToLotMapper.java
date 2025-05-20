package com.auctions.mapper.lot;

import com.auctions.domain.lot.Lot;
import com.auctions.web.api.lot.LotCreateRequest;
import com.auctions.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class LotCreateRequestToLotMapper implements Mapper<LotCreateRequest, Lot> {

    @Override
    public Lot map(LotCreateRequest lotCreateRequest) {

        return Lot.builder()
                .name(lotCreateRequest.getName())
                .surname(lotCreateRequest.getSurname())
                .build();
    }
}
