package com.crm.service.user;

import com.crm.domain.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getUserById(Integer id);
    User createUser(User user);
    User updateUser(Integer id, User userDetails);
    void deleteUser(Integer id);
}
