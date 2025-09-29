package com.ylcnfrht.blockchain.application.dtos.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWalletResponseDto {
    private Long id;
    private String address;
    private String publicKey;
    private String balance;
    private LocalDateTime createdAt;
    private Boolean active;
}
