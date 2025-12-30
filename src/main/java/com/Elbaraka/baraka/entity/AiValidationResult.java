package com.Elbaraka.baraka.entity;

import com.Elbaraka.baraka.enums.AiDecision;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_validation_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiValidationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "operation_id", nullable = false, unique = true)
    private Operation operation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AiDecision decision;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "analysis_summary", columnDefinition = "TEXT")
    private String analysisSummary;

    @Column(name = "extracted_amount", precision = 15, scale = 2)
    private BigDecimal extractedAmount;

    @Column(name = "document_quality_score")
    private Double documentQualityScore;

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    @Column(name = "model_used", length = 50)
    private String modelUsed;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
}
