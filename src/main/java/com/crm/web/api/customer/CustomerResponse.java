package com.crm.web.api.customer;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerResponse {

    private final Long id;
    private final String name;
    private final String surname;
    private final Long createdByUserId;
    private final Long lastModifiedByUserId;
} 