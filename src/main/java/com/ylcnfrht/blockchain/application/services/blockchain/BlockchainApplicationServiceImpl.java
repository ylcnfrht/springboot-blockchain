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
import com.ylcnfrht.blockchain.application.exceptions.BlockchainApplicationException;
import com.ylcnfrht.blockchain.application.mappers.BlockchainDtoMapper;
import com.ylcnfrht.blockchain.application.ports.BlockchainApplicationService;
import com.ylcnfrht.blockchain.application.services.wallet.WalletApplicationService;
import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.blockchain.BlockRepositoryPort;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.domainservices.BlockchainValidationDomainService;
import com.ylcnfrht.blockchain.domain.domainservices.MiningDomainService;
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
      log.info("Successfully retrieved {} blocks", blocks.size());
      return blocks.stream()
          .map(b -> {
            var txs = transactionRepository.findByBlockId(b.getId());
            return blockchainDtoMapper.toBlockResponseDto(b, txs);
          })
          .toList(); 
    } catch (Exception e) {
      log.error("Error getting all blocks", e);
      throw BlockchainApplicationException.getAllBlocksFailed(e.getMessage());
    }
  }

  public Optional<BlockResponseDto> getBlockById(Long id) {
    log.info("Getting block by id: {}", id);
    try {
      Optional<Block> blockOpt = blockRepository.findById(Id.of(id));
      if (blockOpt.isPresent()) {
        log.info("Successfully retrieved block with id: {}", id);
        return blockOpt.map(blockchainDtoMapper::toBlockResponseDto);
      } else {
        log.warn("Block not found with id: {}", id);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting block by id: {}", id, e);
      throw BlockchainApplicationException.getBlockByIdFailed(id, e.getMessage());
    }
  }

  public Optional<BlockResponseDto> getBlockByHash(String hash) {
    log.info("Getting block by hash: {}", hash);
    try {
      Optional<Block> blockOpt = blockRepository.findByHash(Hash.of(hash));
      if (blockOpt.isPresent()) {
        log.info("Successfully retrieved block with hash: {}", hash);
        return blockOpt.map(blockchainDtoMapper::toBlockResponseDto);
      } else {
        log.warn("Block not found with hash: {}", hash);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting block by hash: {}", hash, e);
      throw BlockchainApplicationException.getBlockByHashFailed(hash, e.getMessage());
    }
  }

  public Optional<BlockResponseDto> getLatestBlock() {
    log.info("Getting latest block");
    try {
      Optional<Block> blockOpt = blockRepository.findLatest();
      if (blockOpt.isPresent()) {
        log.info("Successfully retrieved latest block with id: {}", blockOpt.get().getId().getValue());
        return blockOpt.map(blockchainDtoMapper::toBlockResponseDto);
      } else {
        log.warn("No blocks found in blockchain");
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting latest block", e);
      throw BlockchainApplicationException.getLatestBlockFailed(e.getMessage());
    }
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

      // Persist all transactions included in the mined block (including reward)
      log.info("Marking {} transactions as mined (including reward if present)", minedBlock.getTransactions().size());
      for (Transaction tx : minedBlock.getTransactions()) {
        try {
          tx.setMined(true);
          transactionRepository.saveWithBlock(tx, savedBlock);
        } catch (Exception e) {
          log.error("Failed to persist mined transaction with id: {}", tx.getId() != null ? tx.getId().getValue() : null, e);
          throw new RuntimeException("Failed to persist mined transaction: " + e.getMessage(), e);
        }
      }

      log.info("Updating wallet balances after mining");
      walletApplicationService.updateWalletBalancesAfterMining();

      log.info("Block mined successfully with {} transactions", pendingTransactions.size());
      return blockchainDtoMapper.toCreateBlockResponseDto(savedBlock);
    } catch (Exception e) {
      log.error("Error during mining process for miner: {}", minerAddress, e);
      throw BlockchainApplicationException.miningFailed(e.getMessage());
    }
  }

  public boolean isChainValid() {
    log.info("Validating blockchain integrity");
    try {
      List<Block> blocks = blockRepository.findAllOrderByIdDesc();
  
      boolean isValid = blockchainValidationDomainService.isChainValid(blocks, miningDifficulty);
      log.info("Blockchain validation result: {}", isValid ? "VALID" : "INVALID");
      return isValid;
    } catch (Exception e) {
      log.error("Error validating blockchain", e);
      throw BlockchainApplicationException.blockchainValidationFailed(e.getMessage());
    }
  }

  public BlockchainStatsResponseDto getBlockchainStats() {
    log.info("Calculating blockchain statistics");
    try {
      long totalBlocks = blockRepository.count();
      long totalTransactions = transactionRepository.count();
      long pendingTransactions = transactionRepository.findPending().size();
      long totalWallets = walletRepository.count();

      log.info("Blockchain stats - Blocks: {}, Transactions: {}, Pending: {}, Wallets: {}", 
          totalBlocks, totalTransactions, pendingTransactions, totalWallets);
      
      return blockchainDtoMapper.toBlockchainStatsResponseDto(
          totalBlocks, totalTransactions, pendingTransactions, 
          totalWallets, miningDifficulty, miningReward);
    } catch (Exception e) {
      log.error("Error calculating blockchain statistics", e);
      throw BlockchainApplicationException.statsCalculationFailed(e.getMessage());
    }
  }

}