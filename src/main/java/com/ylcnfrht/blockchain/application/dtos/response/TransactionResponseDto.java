package com.ylcnfrht.blockchain.application.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private Long id;
    private String fromAddress;
    private String toAddress;
    private BigDecimal amount;
    private String signature;
    private LocalDateTime timestamp;
    private Boolean mined;
}
