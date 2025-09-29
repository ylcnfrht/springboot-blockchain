package com.ylcnfrht.blockchain.application.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletRequestDto {
    @NotBlank(message = "Address cannot be blank")
    private String address;
    
    @NotBlank(message = "Public key cannot be blank")
    private String publicKey;
    
    @NotBlank(message = "Private key cannot be blank")
    private String privateKey;
}
