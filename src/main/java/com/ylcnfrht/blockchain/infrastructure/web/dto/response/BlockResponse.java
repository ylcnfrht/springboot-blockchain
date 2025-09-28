package com.ylcnfrht.blockchain.infrastructure.web.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ylcnfrht.blockchain.domain.blockchain.Transaction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BlockResponse {
  private Long id;
  private String hash;
  private String previousHash;
  private LocalDateTime timestamp;
  private Integer nonce;
  private Boolean mined;
  private List<Transaction> transactions;
  private Integer transactionCount;
}