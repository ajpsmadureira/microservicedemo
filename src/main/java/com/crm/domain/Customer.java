package com.crm.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Customer {

    private final Long id;
    private final String name;
    private final String surname;
    private final Long createdById;
    private final Long lastModifiedById;
}
