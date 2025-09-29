package com.ylcnfrht.blockchain.application.services.blockchain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.dtos.response.BlockResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.BlockchainStatsResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateBlockResponseDto;
import com.ylcnfrht.blockchain.application.mappers.BlockchainDtoMapper;
import com.ylcnfrht.blockchain.application.ports.BlockchainApplicationService;
import com.ylcnfrht.blockchain.application.services.wallet.WalletApplicationService;
import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.blockchain.BlockRepositoryPort;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.TransactionRepositoryPort;
import com.ylcnfrht.blockchain.domain.wallet.WalletRepositoryPort;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlockchainApplicationServiceImpl implements BlockchainApplicationService {

  private final BlockRepositoryPort blockRepository;
  private final TransactionRepositoryPort transactionRepository;
  private final WalletRepositoryPort walletRepository;
  private final WalletApplicationService walletApplicationService;
  private final BlockchainDtoMapper blockchainDtoMapper;

  @Value("${blockchain.mining.difficulty:4}")
  private int miningDifficulty;

  @Value("${blockchain.mining.reward:100}")
  private BigDecimal miningReward;

  public List<BlockResponseDto> getAllBlocks() {
    return blockRepository.findAllOrderByIdDesc().stream()
        .map(blockchainDtoMapper::toBlockResponseDto)
        .toList();
  }

  public Optional<BlockResponseDto> getBlockById(Long id) {
    return blockRepository.findById(Id.of(id))
        .map(blockchainDtoMapper::toBlockResponseDto);
  }

  public Optional<BlockResponseDto> getBlockByHash(String hash) {
    return blockRepository.findByHash(Hash.of(hash))
        .map(blockchainDtoMapper::toBlockResponseDto);
  }

  public Optional<BlockResponseDto> getLatestBlock() {
    return blockRepository.findLatest()
        .map(blockchainDtoMapper::toBlockResponseDto);
  }

  @Transactional
  public CreateBlockResponseDto minePendingTransactions(String minerAddress) {
    List<Transaction> pendingTransactions = transactionRepository.findPending();

    Hash previousHash = blockRepository.findLatest()
        .map(Block::getHash)
        .orElse(null);

    Block newBlock = Block.create(previousHash);

    for (Transaction tx : pendingTransactions) {
      if (tx.isValid()) newBlock.addTransaction(tx);
    }

    while (!newBlock.meetsDifficulty(miningDifficulty)) {
      newBlock.incrementNonce();
    }
    newBlock.markAsMined(miningDifficulty);

    Block savedBlock = blockRepository.save(newBlock);

    for (Transaction tx : pendingTransactions) {
      tx.setMined(true);
      transactionRepository.save(tx);
    }

    walletApplicationService.updateWalletBalancesAfterMining();

    log.info("Block mined successfully with {} transactions", pendingTransactions.size());
    return blockchainDtoMapper.toCreateBlockResponseDto(savedBlock);
  }

  public boolean isChainValid() {
    List<Block> blocks = blockRepository.findAllOrderByIdDesc();
    for (int i = 1; i < blocks.size(); i++) {
      Block currentBlock = blocks.get(i);
      Block previousBlock = blocks.get(i - 1);
      if (!currentBlock.isValid()) return false;
      if (currentBlock.getPreviousHash() == null || previousBlock.getHash() == null) return false;
      if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) return false;
    }
    return true;
  }

  public BlockchainStatsResponseDto getBlockchainStats() {
    long totalBlocks = blockRepository.count();
    long totalTransactions = transactionRepository.count();
    long pendingTransactions = transactionRepository.findPending().size();
    long totalWallets = walletRepository.count();

    return blockchainDtoMapper.toBlockchainStatsResponseDto(
        totalBlocks, totalTransactions, pendingTransactions, 
        totalWallets, miningDifficulty, miningReward);
  }

}