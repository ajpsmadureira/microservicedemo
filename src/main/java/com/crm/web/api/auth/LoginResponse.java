package com.crm.web.api.auth;

import lombok.Value;

@Value(staticConstructor = "create")
public class LoginResponse {

    String token;
} 