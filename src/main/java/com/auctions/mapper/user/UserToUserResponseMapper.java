package com.auctions.mapper.user;

import com.auctions.domain.User;
import com.auctions.web.api.user.UserResponse;
import com.auctions.mapper.Mapper;
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
