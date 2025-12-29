package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.dto.OperationResponse;
import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.service.OperationService;
import com.Elbaraka.baraka.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final OperationService operationService;

    public AdminController(UserService userService, OperationService operationService) {
        this.userService = userService;
        this.operationService = operationService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}/activate")
    public ResponseEntity<User> activateUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        user.setActive(true);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<User> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationResponse>> getAllOperations() {
        List<OperationResponse> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations);
    }
}
