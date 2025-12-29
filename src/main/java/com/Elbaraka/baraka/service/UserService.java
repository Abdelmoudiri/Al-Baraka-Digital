package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    User getUserById(Long id);

    User getUserByEmail(String email);

    List<User> getAllUsers();

    User updateUser(Long id, User user);

    void deactivateUser(Long id);

    void changeUserRole(Long id, String roleName);
}
