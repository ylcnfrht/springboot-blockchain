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
        .id(block.getId() != null ? block.getId().getValue() : null)
        .hash(block.getHash() != null ? block.getHash().getValue() : null)
        .previousHash(block.getPreviousHash() != null ? block.getPreviousHash().getValue() : null)
        .nonce(block.getNonce() != null ? block.getNonce().getValue() : null)
        .timestamp(block.getTimestamp() != null ? block.getTimestamp().getValue() : null)
        .mined(block.isMined())
        .transactions(block.getTransactions().stream()
            .map(this::toTransactionResponseDto)
            .toList())
        .build();
  }

  public BlockResponseDto toBlockResponseDto(Block block) {
    return BlockResponseDto.builder()
        .id(block.getId() != null ? block.getId().getValue() : null)
        .hash(block.getHash() != null ? block.getHash().getValue() : null)
        .previousHash(block.getPreviousHash() != null ? block.getPreviousHash().getValue() : null)
        .nonce(block.getNonce() != null ? block.getNonce().getValue() : null)
        .timestamp(block.getTimestamp() != null ? block.getTimestamp().getValue() : null)
        .mined(block.isMined())
        .transactions(block.getTransactions().stream()
            .map(this::toTransactionResponseDto)
            .toList())
        .build();
  }

  public BlockResponseDto toBlockResponseDto(Block block, java.util.List<Transaction> transactions) {
    return BlockResponseDto.builder()
        .id(block.getId() != null ? block.getId().getValue() : null)
        .hash(block.getHash() != null ? block.getHash().getValue() : null)
        .previousHash(block.getPreviousHash() != null ? block.getPreviousHash().getValue() : null)
        .nonce(block.getNonce() != null ? block.getNonce().getValue() : null)
        .timestamp(block.getTimestamp() != null ? block.getTimestamp().getValue() : null)
        .mined(block.isMined())
        .transactions(transactions.stream().map(this::toTransactionResponseDto).toList())
        .build();
  }

  public TransactionResponseDto toTransactionResponseDto(Transaction transaction) {
    return TransactionResponseDto.builder()
        .id(transaction.getId() != null ? transaction.getId().getValue() : null)
        .fromAddress(transaction.getFromAddress() != null ? transaction.getFromAddress().getValue() : null)
        .toAddress(transaction.getToAddress() != null ? transaction.getToAddress().getValue() : null)
        .amount(transaction.getAmount() != null ? transaction.getAmount().getValue() : null)
        .signature(transaction.getSignature() != null ? transaction.getSignature().getValue() : null)
        .timestamp(transaction.getTimestamp() != null ? transaction.getTimestamp().getValue() : null)
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
