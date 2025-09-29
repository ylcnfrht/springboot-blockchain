package com.ylcnfrht.blockchain.application.ports;

import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.application.dtos.response.BlockResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.BlockchainStatsResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateBlockResponseDto;

public interface BlockchainApplicationService {
  public List<BlockResponseDto> getAllBlocks();
  public Optional<BlockResponseDto> getBlockById(Long id);
  public Optional<BlockResponseDto> getBlockByHash(String hash);
  public Optional<BlockResponseDto> getLatestBlock();
  public CreateBlockResponseDto minePendingTransactions(String minerAddress);
  public boolean isChainValid();
  public BlockchainStatsResponseDto getBlockchainStats();
}