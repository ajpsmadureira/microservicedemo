package com.crm.mapper.user;

import com.crm.domain.User;
import com.crm.web.api.user.UserUpdateRequest;
import com.crm.mapper.Mapper;
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
