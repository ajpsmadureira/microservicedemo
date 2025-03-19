package com.crm.mapper.lot;

import com.crm.domain.Lot;
import com.crm.web.api.lot.LotResponse;
import com.crm.mapper.Mapper;
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
