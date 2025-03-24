package com.auctions.web.api.user;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserResponse {

    private final Integer id;
    private final String username;
    private final String email;
    private final Boolean isAdmin;
    private final Boolean isActive;
}
