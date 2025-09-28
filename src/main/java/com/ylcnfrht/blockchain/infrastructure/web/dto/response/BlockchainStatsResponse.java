package com.ylcnfrht.blockchain.infrastructure.web.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response object containing comprehensive blockchain statistics")
public class BlockchainStatsResponse {

  @Schema(description = "Total number of blocks in the blockchain", example = "1520")
  private Long totalBlocks;

  @Schema(description = "Total number of transactions processed", example = "8967")
  private Long totalTransactions;

  @Schema(description = "Number of transactions waiting to be mined", example = "23")
  private Long pendingTransactions;

  @Schema(description = "Total number of wallets created", example = "456")
  private Long totalWallets;

  @Schema(description = "Current mining difficulty level", example = "4")
  private Integer difficulty;

  @Schema(description = "Reward amount for mining a block", example = "100.0")
  private BigDecimal miningReward;
}
