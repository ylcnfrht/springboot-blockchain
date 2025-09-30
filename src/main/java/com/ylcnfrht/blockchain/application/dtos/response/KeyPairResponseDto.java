package com.ylcnfrht.blockchain.application.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyPairResponseDto {
    private String publicKey;
    private String privateKey;
    private String algorithm;
    private String message;
}
