package com.auctions.mapper.user;

import com.auctions.domain.User;
import com.auctions.persistence.entity.UserEntity;
import com.auctions.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToUserMapper implements Mapper<UserEntity, User> {

    @Override
    public User map(UserEntity userEntity) {

        return User.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .isActive(userEntity.isActive())
                .isAdmin(userEntity.isAdmin())
                .username(userEntity.getUsername())
                .build();
    }
}
