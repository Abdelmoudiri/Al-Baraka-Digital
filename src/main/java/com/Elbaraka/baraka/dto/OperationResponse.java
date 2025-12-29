package com.Elbaraka.baraka.dto;

import com.Elbaraka.baraka.enums.OperationStatus;
import com.Elbaraka.baraka.enums.OperationType;
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
public class OperationResponse {

    private Long id;
    private OperationType type;
    private BigDecimal amount;
    private OperationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime executedAt;
    private String sourceAccountNumber;
    private String destinationAccountNumber;
}
