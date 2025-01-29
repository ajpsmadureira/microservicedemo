package com.crm.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Boolean isAdmin;
    private final Boolean isActive;
}
