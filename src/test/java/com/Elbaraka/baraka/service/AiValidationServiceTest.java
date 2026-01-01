package com.Elbaraka.baraka.service;

import com.Elbaraka.baraka.entity.AiValidationResult;
import com.Elbaraka.baraka.entity.Operation;
import com.Elbaraka.baraka.entity.Account;
import com.Elbaraka.baraka.enums.AiDecision;
import com.Elbaraka.baraka.repository.AiValidationResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AiValidationService.
 * Tests AI validation logic for banking operations.
 */
@ExtendWith(MockitoExtension.class)
class AiValidationServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private AiValidationResultRepository validationRepository;

    @InjectMocks
    private AiValidationService aiValidationService;

    private Operation testOperation;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountNumber("ACC123456");
        testAccount.setBalance(BigDecimal.valueOf(10000));

        testOperation = new Operation();
        testOperation.setId(1L);
        testOperation.setAmount(BigDecimal.valueOf(500));
        testOperation.setAccount(testAccount);
    }

    @Test
    void testAnalyzeOperation_Success() {
        // Given
        String mockAiResponse = """
            {
                "decision": "APPROVE",
                "confidenceScore": 95.5,
                "analysisSummary": "Valid transaction with clear documentation",
                "riskFactors": [],
                "recommendations": ["Process immediately"]
            }
            """;

        ChatResponse chatResponse = mock(ChatResponse.class);
        when(chatClient.call(anyString())).thenReturn(chatResponse);
        when(chatResponse.getResult()).thenReturn(mockAiResponse);

        AiValidationResult expectedResult = AiValidationResult.builder()
                .decision(AiDecision.APPROVE)
                .confidenceScore(95.5)
                .build();

        when(validationRepository.save(any(AiValidationResult.class))).thenReturn(expectedResult);

        // When
        AiValidationResult result = aiValidationService.analyzeOperation(testOperation);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDecision()).isEqualTo(AiDecision.APPROVE);
        assertThat(result.getConfidenceScore()).isGreaterThan(90.0);

        verify(validationRepository, times(1)).save(any(AiValidationResult.class));
    }

    @Test
    void testGetValidationStatistics() {
        // Given
        when(validationRepository.countByDecision(AiDecision.APPROVE)).thenReturn(150L);
        when(validationRepository.countByDecision(AiDecision.REJECT)).thenReturn(30L);
        when(validationRepository.countByDecision(AiDecision.NEED_HUMAN_REVIEW)).thenReturn(20L);

        // When
        var stats = aiValidationService.getValidationStatistics();

        // Then
        assertThat(stats).containsKey("approved");
        assertThat(stats).containsKey("rejected");
        assertThat(stats).containsKey("needsReview");
        assertThat(stats.get("approved")).isEqualTo(150L);
        assertThat(stats.get("rejected")).isEqualTo(30L);

        verify(validationRepository, times(3)).countByDecision(any(AiDecision.class));
    }
}
