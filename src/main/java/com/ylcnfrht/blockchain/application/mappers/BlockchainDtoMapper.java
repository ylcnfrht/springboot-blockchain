package com.ylcnfrht.blockchain.application.mappers;

import org.springframework.stereotype.Component;

import com.ylcnfrht.blockchain.application.dtos.response.BlockResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.BlockchainStatsResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateBlockResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;
import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;

@Component
public class BlockchainDtoMapper {

  public CreateBlockResponseDto toCreateBlockResponseDto(Block block) {
    return CreateBlockResponseDto.builder()
        .id(block.getId().getValue())
        .hash(block.getHash().getValue())
        .previousHash(block.getPreviousHash() != null ? block.getPreviousHash().getValue() : null)
        .nonce(block.getNonce().getValue())
        .timestamp(block.getTimestamp().getValue())
        .mined(block.isMined())
        .transactions(block.getTransactions().stream()
            .map(this::toTransactionResponseDto)
            .toList())
        .build();
  }

  public BlockResponseDto toBlockResponseDto(Block block) {
    return BlockResponseDto.builder()
        .id(block.getId().getValue())
        .hash(block.getHash().getValue())
        .previousHash(block.getPreviousHash() != null ? block.getPreviousHash().getValue() : null)
        .nonce(block.getNonce().getValue())
        .timestamp(block.getTimestamp().getValue())
        .mined(block.isMined())
        .transactions(block.getTransactions().stream()
            .map(this::toTransactionResponseDto)
            .toList())
        .build();
  }

  public TransactionResponseDto toTransactionResponseDto(Transaction transaction) {
    return TransactionResponseDto.builder()
        .id(transaction.getId().getValue())
        .fromAddress(transaction.getFromAddress() != null ? transaction.getFromAddress().getValue() : null)
        .toAddress(transaction.getToAddress().getValue())
        .amount(transaction.getAmount().getValue())
        .signature(transaction.getSignature() != null ? transaction.getSignature().getValue() : null)
        .timestamp(transaction.getTimestamp().getValue())
        .mined(transaction.isMined())
        .build();
  }

  public BlockchainStatsResponseDto toBlockchainStatsResponseDto(
      long totalBlocks, 
      long totalTransactions, 
      long pendingTransactions, 
      long totalWallets, 
      int difficulty, 
      java.math.BigDecimal miningReward) {
    return BlockchainStatsResponseDto.builder()
        .totalBlocks(totalBlocks)
        .totalTransactions(totalTransactions)
        .pendingTransactions(pendingTransactions)
        .totalWallets(totalWallets)
        .difficulty(difficulty)
        .miningReward(miningReward)
        .build();
  }
}
