package com.ylcnfrht.blockchain.infrastructure.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request object for creating a new wallet")
public class CreateWalletRequest {

  @NotBlank(message = "Address is required")
  @Schema(description = "Unique blockchain address for the wallet", example = "04a1b2c3d4e5f6789...", required = true)
  private String address;

  @NotBlank(message = "Public key is required")
  @Schema(description = "Public key for cryptographic operations", example = "04f6e5d4c3b2a1987...", required = true)
  private String publicKey;

  @Schema(description = "Private key (optional, for wallet import)", example = "L1a2b3c4d5e6f7g8h9...", nullable = true)
  private String privateKey;
}