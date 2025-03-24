package com.auctions.mapper.user;

import com.auctions.domain.User;
import com.auctions.web.api.user.UserUpdateRequest;
import com.auctions.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateRequestToUserMapper implements Mapper<UserUpdateRequest, User> {

    @Override
    public User map(UserUpdateRequest userUpdateRequest) {

        return User.builder()
                .email(userUpdateRequest.getEmail())
                .password(userUpdateRequest.getPassword())
                .isAdmin(userUpdateRequest.getIsAdmin())
                .username(userUpdateRequest.getUsername())
                .build();
    }
}
