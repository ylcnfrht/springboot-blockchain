package com.ylcnfrht.blockchain.application.blockchain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.wallet.WalletApplicationService;
import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.blockchain.Transaction;
import com.ylcnfrht.blockchain.infrastructure.persistence.BlockRepository;
import com.ylcnfrht.blockchain.infrastructure.persistence.TransactionRepository;
import com.ylcnfrht.blockchain.infrastructure.persistence.WalletRepository;
import com.ylcnfrht.blockchain.infrastructure.web.dto.response.BlockchainStatsResponse;
import com.ylcnfrht.blockchain.shared.HashUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BlockchainApplicationService {

  private final BlockRepository blockRepository;
  private final TransactionRepository transactionRepository;
  private final WalletRepository walletRepository;
  private final WalletApplicationService walletApplicationService;

  @Value("${blockchain.mining.difficulty:4}")
  private int miningDifficulty;

  @Value("${blockchain.mining.reward:100}")
  private BigDecimal miningReward;

  public List<Block> getAllBlocks() {
    return blockRepository.findAllOrderByIdDesc();
  }

  public Optional<Block> getBlockById(Long id) {
    return blockRepository.findById(id);
  }

  public Optional<Block> getBlockByHash(String hash) {
    return blockRepository.findByHash(hash);
  }

  public Optional<Block> getLatestBlock() {
    return blockRepository.findLatestBlock();
  }

  @Transactional
  public Block minePendingTransactions(String minerAddress) {
    List<Transaction> pendingTransactions = transactionRepository.findByBlockIsNull();

    Transaction rewardTransaction = Transaction.builder()
        .toAddress(minerAddress)
        .amount(miningReward)
        .timestamp(LocalDateTime.now())
        .transactionHash(HashUtils.createHash(minerAddress + miningReward + System.currentTimeMillis()))
        .build();

    transactionRepository.save(rewardTransaction);
    pendingTransactions.add(rewardTransaction);

    String previousHash = getLatestBlock()
        .map(Block::getHash)
        .orElse("0");

    Block newBlock = Block.builder()
        .previousHash(previousHash)
        .timestamp(LocalDateTime.now())
        .build();

    // Calculate initial hash
    newBlock.setHash(calculateBlockHash(newBlock));

    // Mine the block
    mineBlock(newBlock, miningDifficulty);

    Block savedBlock = blockRepository.save(newBlock);

    // Associate transactions with the block and mark as mined
    pendingTransactions.forEach(tx -> {
      tx.setBlock(savedBlock);
      tx.setMined(true);
      transactionRepository.save(tx);
    });

    // Update wallet balances after mining
    walletApplicationService.updateWalletBalancesAfterMining();

    log.info("Block mined successfully with {} transactions", pendingTransactions.size());
    return savedBlock;
  }

  private void mineBlock(Block block, int difficulty) {
    String target = "0".repeat(difficulty);

    while (!block.getHash().substring(0, difficulty).equals(target)) {
      block.setNonce(block.getNonce() + 1);
      block.setHash(calculateBlockHash(block));
    }

    block.setMined(true);
    log.info("Block mined: {}", block.getHash());
  }

  private String calculateBlockHash(Block block) {
    String data = block.getPreviousHash() +
        block.getTimestamp().toString() +
        block.getNonce() +
        block.getTransactions().toString();
    return HashUtils.createHash(data);
  }

  public boolean isChainValid() {
    List<Block> blocks = blockRepository.findAllOrderByIdDesc();

    for (int i = 1; i < blocks.size(); i++) {
      Block currentBlock = blocks.get(i);
      Block previousBlock = blocks.get(i - 1);

      if (!currentBlock.getHash().equals(calculateBlockHash(currentBlock))) {
        return false;
      }

      if (!currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
        return false;
      }
    }

    return true;
  }

  public BlockchainStatsResponse getBlockchainStats() {
    long totalBlocks = blockRepository.count();
    long totalTransactions = transactionRepository.count();
    long pendingTransactions = transactionRepository.findByBlockIsNull().size();
    long totalWallets = walletRepository.count();

    return BlockchainStatsResponse.builder()
        .totalBlocks(totalBlocks)
        .totalTransactions(totalTransactions)
        .pendingTransactions(pendingTransactions)
        .totalWallets(totalWallets)
        .difficulty(miningDifficulty)
        .miningReward(miningReward)
        .build();
  }
}