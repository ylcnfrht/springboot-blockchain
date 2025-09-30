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
import com.ylcnfrht.blockchain.domain.services.BlockchainValidationDomainService;
import com.ylcnfrht.blockchain.domain.services.MiningDomainService;
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
  private final MiningDomainService miningDomainService;
  private final BlockchainValidationDomainService blockchainValidationDomainService;

  @Value("${blockchain.mining.difficulty:4}")
  private int miningDifficulty;

  @Value("${blockchain.mining.reward:100}")
  private BigDecimal miningReward;

  public List<BlockResponseDto> getAllBlocks() {
    log.info("Getting all blocks from repository");
    try {
      List<Block> blocks = blockRepository.findAllOrderByIdDesc();
      log.info("Found {} blocks", blocks.size());
      return blocks.stream()
          .map(blockchainDtoMapper::toBlockResponseDto)
          .toList(); 
    } catch (Exception e) {
      log.error("Error getting blocks", e);
      throw e;
    }
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
    log.info("Starting mining process for miner: {}", minerAddress);
    
    try {
      log.info("Fetching pending transactions");
      List<Transaction> pendingTransactions = transactionRepository.findPending();
      log.info("Found {} pending transactions", pendingTransactions.size());

      log.info("Fetching latest block for previous hash");
      Hash previousHash = blockRepository.findLatest()
          .map(Block::getHash)
          .orElse(null);
      log.info("Previous hash: {}", previousHash != null ? previousHash.getValue() : "null");

      log.info("Starting mining with difficulty: {} and reward: {}", miningDifficulty, miningReward);
      // Use domain service for mining
      Block minedBlock = miningDomainService.mineBlock(
          pendingTransactions, 
          previousHash, 
          miningDifficulty, 
          miningReward, 
          minerAddress
      );
      log.info("Block mined successfully: {}", minedBlock);

      log.info("Saving mined block to repository");
      Block savedBlock = blockRepository.save(minedBlock);
      log.info("Block saved with ID: {}", savedBlock.getId());

      log.info("Marking {} transactions as mined", pendingTransactions.size());
      // Mark transactions as mined
      for (Transaction tx : pendingTransactions) {
        tx.setMined(true);
        transactionRepository.save(tx);
      }

      log.info("Updating wallet balances after mining");
      walletApplicationService.updateWalletBalancesAfterMining();

      log.info("Block mined successfully with {} transactions", pendingTransactions.size());
      return blockchainDtoMapper.toCreateBlockResponseDto(savedBlock);
    } catch (Exception e) {
      log.error("Error during mining process", e);
      throw e;
    }
  }

  public boolean isChainValid() {
    List<Block> blocks = blockRepository.findAllOrderByIdDesc();
    return blockchainValidationDomainService.isChainValid(blocks);
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