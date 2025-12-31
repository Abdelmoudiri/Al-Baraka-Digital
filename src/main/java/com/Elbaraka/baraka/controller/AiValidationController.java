package com.Elbaraka.baraka.controller;

import com.Elbaraka.baraka.dto.AiValidationResponse;
import com.Elbaraka.baraka.entity.AiValidationResult;
import com.Elbaraka.baraka.service.AiValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiValidationController {

    private final AiValidationService aiValidationService;

    @GetMapping("/validation/{operationId}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<AiValidationResponse> getValidationResult(@PathVariable Long operationId) {
        AiValidationResult result = aiValidationService.getValidationResult(operationId);
        
        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        AiValidationResponse response = mapToResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = aiValidationService.getValidationStatistics();
        return ResponseEntity.ok(stats);
    }

    private AiValidationResponse mapToResponse(AiValidationResult result) {
        return AiValidationResponse.builder()
                .id(result.getId())
                .operationId(result.getOperation().getId())
                .decision(result.getDecision())
                .confidenceScore(result.getConfidenceScore())
                .analysisSummary(result.getAnalysisSummary())
                .extractedAmount(result.getExtractedAmount())
                .documentQualityScore(result.getDocumentQualityScore())
                .riskFactors(result.getRiskFactors())
                .analyzedAt(result.getAnalyzedAt())
                .modelUsed(result.getModelUsed())
                .processingTimeMs(result.getProcessingTimeMs())
                .build();
    }
}
