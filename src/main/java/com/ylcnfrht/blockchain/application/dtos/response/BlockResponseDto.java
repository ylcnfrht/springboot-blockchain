package com.ylcnfrht.blockchain.application.dtos.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockResponseDto {
    private Long id;
    private String hash;
    private String previousHash;
    private Long nonce;
    private LocalDateTime timestamp;
    private Boolean mined;
    private List<TransactionResponseDto> transactions;
}
