package com.auctions.web.api.lot;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LotResponse {

    private final Integer id;
    private final String name;
    private final String surname;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
} 