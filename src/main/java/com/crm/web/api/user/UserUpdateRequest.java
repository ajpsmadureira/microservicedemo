package com.crm.web.api.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserUpdateRequest {

    @Size(min = 2, max = 50, message = "Username must be between 2 and 50 characters")
    private final String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private final String password;

    @Email(message = "Email should be valid")
    private final String email;

    private final Boolean isAdmin;
}
