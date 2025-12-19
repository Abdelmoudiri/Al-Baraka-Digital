package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService u)
    {
        this.userService=u;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAll()
    {
        return ResponseEntity.ok(userService.getAllUsers());
//        List<User> all=userService.getAllUsers();
//        return ResponseEntity.status(HttpStatus.OK).body(all);
    }
}
