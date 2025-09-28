package com.ylcnfrht.blockchain.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for mining a new block")
public class MineBlockRequest {

  @NotBlank(message = "Miner address is required")
  @Schema(description = "Wallet address that will receive the mining reward", example = "04a1b2c3d4e5f6789...", required = true)
  private String minerAddress;
}