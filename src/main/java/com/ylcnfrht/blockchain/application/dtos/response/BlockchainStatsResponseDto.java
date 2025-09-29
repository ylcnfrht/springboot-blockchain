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
public class BlockchainStatsResponseDto {
    private long totalBlocks;
    private long totalTransactions;
    private long pendingTransactions;
    private long totalWallets;
    private int difficulty;
    private BigDecimal miningReward;
}
