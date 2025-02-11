package com.crm.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Customer {

    private final Integer id;
    private final String name;
    private final String surname;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
}
