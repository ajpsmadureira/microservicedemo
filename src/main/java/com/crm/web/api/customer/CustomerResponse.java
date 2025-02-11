package com.crm.web.api.customer;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerResponse {

    private final Integer id;
    private final String name;
    private final String surname;
    private final Integer createdByUserId;
    private final Integer lastModifiedByUserId;
} 