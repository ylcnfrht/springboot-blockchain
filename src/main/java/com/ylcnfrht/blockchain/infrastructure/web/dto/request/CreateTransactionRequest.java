package com.ylcnfrht.blockchain.infrastructure.web.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new transaction")
public class CreateTransactionRequest {

  @Schema(description = "Source wallet address (null for mining rewards)", example = "04a1b2c3d4e5f6...", nullable = true)
  private String fromAddress;

  @NotBlank(message = "To address is required")
  @Schema(description = "Destination wallet address", example = "04f6e5d4c3b2a1...", required = true)
  private String toAddress;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
  @Schema(description = "Transaction amount in blockchain currency", example = "25.50", required = true, minimum = "0")
  private BigDecimal amount;

  @Schema(description = "Digital signature of the transaction", example = "304402201234abcd...", nullable = true)
  private String signature;
}