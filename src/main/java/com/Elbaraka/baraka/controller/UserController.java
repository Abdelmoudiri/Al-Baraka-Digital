package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.service.UserService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService u)
    {
        this.userService=u;
    }


    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAll()
    {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user)
    {
        return ResponseEntity.ok(userService.updateUser(id,user));

    }

    @GetMapping("users/{id}")
    public User getUser(@PathVariable Long id)
    {
        return userService.getUserById(id);
    }

    @PostMapping("admin/user")
    public ResponseEntity<User> creatUser(@Valid @RequestBody User user)
    {
        return ResponseEntity.ok(userService.createUser(user));
    }
}
