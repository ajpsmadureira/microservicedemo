package com.crm.mapper.user;

import com.crm.domain.User;
import com.crm.persistence.entity.UserEntity;
import com.crm.mapper.Mapper;
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
