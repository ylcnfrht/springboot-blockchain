package com.ylcnfrht.blockchain.infrastructure.web.dto.response;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response object containing wallet balance information")
public class WalletBalanceResponse {

  @Schema(description = "Wallet address", example = "04a1b2c3d4e5f6789...")
  private String address;

  @Schema(description = "Confirmed balance from mined transactions", example = "150.75")
  private BigDecimal balance;

  @Schema(description = "Pending balance from unmined transactions", example = "25.50")
  private BigDecimal pendingBalance;
}