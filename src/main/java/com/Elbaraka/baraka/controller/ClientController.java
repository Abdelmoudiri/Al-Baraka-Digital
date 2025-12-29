package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.dto.ClientProfileResponse;
import com.Elbaraka.baraka.dto.OperationRequest;
import com.Elbaraka.baraka.dto.OperationResponse;
import com.Elbaraka.baraka.dto.TransferRequest;
import com.Elbaraka.baraka.entity.User;
import com.Elbaraka.baraka.service.OperationService;
import com.Elbaraka.baraka.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@PreAuthorize("hasRole('CLIENT')")
public class ClientController {

    private final UserService userService;
    private final OperationService operationService;

    public ClientController(UserService userService, OperationService operationService) {
        this.userService = userService;
        this.operationService = operationService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ClientProfileResponse> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        User user = userService.getUserByEmail(email);
        
        ClientProfileResponse response = ClientProfileResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .accountNumber(user.getAccount() != null ? user.getAccount().getAccountNumber() : null)
                .balance(user.getAccount() != null ? user.getAccount().getBalance() : null)
                .active(user.getActive())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/operations/deposit")
    public ResponseEntity<OperationResponse> createDeposit(@Valid @RequestBody OperationRequest request) {
        OperationResponse response = operationService.createDeposit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/operations/withdrawal")
    public ResponseEntity<OperationResponse> createWithdrawal(@Valid @RequestBody OperationRequest request) {
        OperationResponse response = operationService.createWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/operations/transfer")
    public ResponseEntity<OperationResponse> createTransfer(@Valid @RequestBody TransferRequest request) {
        OperationResponse response = operationService.createTransfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationResponse>> getOperations() {
        List<OperationResponse> operations = operationService.getClientOperations();
        return ResponseEntity.ok(operations);
    }
}
