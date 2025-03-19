package com.crm.mapper.lot;

import com.crm.domain.Lot;
import com.crm.web.api.lot.LotCreateRequest;
import com.crm.mapper.Mapper;
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
