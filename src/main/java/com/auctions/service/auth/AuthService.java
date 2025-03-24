package com.auctions.service.auth;

import com.auctions.domain.User;

public interface AuthService {

    String login(String username, String password);
    User getCurrentUser();
}
