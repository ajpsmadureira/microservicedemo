package com.crm.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class User {

    private final Long id;
    private final String username;
    private final String password;
    private final String email;
    private final Boolean isAdmin;
    private final Boolean isActive;
}
