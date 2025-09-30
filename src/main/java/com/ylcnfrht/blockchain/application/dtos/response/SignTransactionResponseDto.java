package com.ylcnfrht.blockchain.application.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignTransactionResponseDto {
    private Long transactionId;
    private String signature;
    private boolean signed;
    private String message;
}
