package com.crm.service.auth;

import com.crm.domain.User;

public interface AuthService {

    String login(String username, String password);
    User getCurrentUser();
}
