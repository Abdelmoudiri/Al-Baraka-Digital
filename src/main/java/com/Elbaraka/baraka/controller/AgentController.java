package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.dto.OperationResponse;
import com.Elbaraka.baraka.service.OperationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
@PreAuthorize("hasRole('AGENT')")
public class AgentController {

    private final OperationService operationService;

    public AgentController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping("/operations/pending")
    public ResponseEntity<List<OperationResponse>> getPendingOperations() {
        List<OperationResponse> pendingOperations = operationService.getPendingOperations();
        return ResponseEntity.ok(pendingOperations);
    }

    @PutMapping("/operations/{id}/approve")
    public ResponseEntity<OperationResponse> approveOperation(@PathVariable Long id) {
        OperationResponse response = operationService.approveOperation(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/operations/{id}/reject")
    public ResponseEntity<OperationResponse> rejectOperation(@PathVariable Long id) {
        OperationResponse response = operationService.rejectOperation(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/operations")
    public ResponseEntity<List<OperationResponse>> getAllOperations() {
        List<OperationResponse> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations);
    }
}
