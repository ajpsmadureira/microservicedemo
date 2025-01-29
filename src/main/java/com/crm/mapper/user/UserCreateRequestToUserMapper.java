package com.crm.mapper.user;

import com.crm.domain.User;
import com.crm.web.api.user.UserCreateRequest;
import com.crm.mapper.Mapper;
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
