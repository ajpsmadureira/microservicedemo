package com.crm.mapper.user;

import com.crm.domain.User;
import com.crm.web.api.user.UserResponse;
import com.crm.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserToUserResponseMapper implements Mapper<User, UserResponse> {

    @Override
    public UserResponse map(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .isActive(user.getIsActive())
                .isAdmin(user.getIsAdmin())
                .build();
    }
}
