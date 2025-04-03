package com.auctions.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Lot {

    private final Integer id;
    private final String name;
    private final String surname;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
}
