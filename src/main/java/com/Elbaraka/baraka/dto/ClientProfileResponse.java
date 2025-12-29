package com.Elbaraka.baraka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileResponse {

    private Long userId;
    private String email;
    private String fullName;
    private String accountNumber;
    private BigDecimal balance;
    private Boolean active;
}
