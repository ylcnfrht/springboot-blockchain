package com.ylcnfrht.blockchain.application.dtos.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletBalanceResponseDto {
    private String address;
    private BigDecimal balance;
    private BigDecimal pendingBalance;
}
