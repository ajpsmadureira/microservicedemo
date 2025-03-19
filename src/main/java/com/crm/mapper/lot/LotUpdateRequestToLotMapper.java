package com.crm.mapper.lot;

import com.crm.domain.Lot;
import com.crm.mapper.Mapper;
import com.crm.web.api.lot.LotUpdateRequest;
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
