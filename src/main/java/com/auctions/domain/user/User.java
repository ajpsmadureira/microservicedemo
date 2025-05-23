package com.auctions.domain.user;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class User {

    private final Integer id;
    private final String username;
    private final String password;
    private final String email;
    private final Boolean isAdmin;
    private final Boolean isActive;
}
