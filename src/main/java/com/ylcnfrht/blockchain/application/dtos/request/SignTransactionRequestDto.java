package com.ylcnfrht.blockchain.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignTransactionRequestDto {
    
    @NotNull(message = "Transaction ID cannot be null")
    private Long transactionId;
    
    @NotBlank(message = "Private key cannot be blank")
    private String privateKey;
}
