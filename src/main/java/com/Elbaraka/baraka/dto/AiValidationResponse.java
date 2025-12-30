package com.Elbaraka.baraka.dto;

import com.Elbaraka.baraka.enums.AiDecision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiValidationResponse {
    private Long id;
    private Long operationId;
    private AiDecision decision;
    private Double confidenceScore;
    private String analysisSummary;
    private BigDecimal extractedAmount;
    private Double documentQualityScore;
    private String riskFactors;
    private LocalDateTime analyzedAt;
    private String modelUsed;
    private Long processingTimeMs;
}
