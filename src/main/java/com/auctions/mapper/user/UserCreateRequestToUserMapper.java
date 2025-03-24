package com.auctions.mapper.user;

import com.auctions.domain.User;
import com.auctions.web.api.user.UserCreateRequest;
import com.auctions.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserCreateRequestToUserMapper implements Mapper<UserCreateRequest, User> {

    @Override
    public User map(UserCreateRequest userCreateRequest) {

        return User.builder()
                .email(userCreateRequest.getEmail())
                .password(userCreateRequest.getPassword())
                .isAdmin(userCreateRequest.getIsAdmin())
                .username(userCreateRequest.getUsername())
                .build();
    }
}
