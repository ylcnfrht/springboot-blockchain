package com.ylcnfrht.blockchain.domain.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

import lombok.extern.slf4j.Slf4j;

/**
 * Domain service responsible for blockchain mining operations.
 * Contains the core mining algorithm and proof-of-work logic.
 */
@Service
@Slf4j
public class MiningDomainService {

    /**
     * Mines a new block with pending transactions using proof-of-work algorithm.
     * 
     * @param pendingTransactions list of transactions to include in the block
     * @param previousHash hash of the previous block in the chain
     * @param difficulty mining difficulty level
     * @param miningReward reward amount for the miner
     * @param minerAddress address of the miner who will receive the reward
     * @return the newly mined block
     */
    public Block mineBlock(List<Transaction> pendingTransactions, 
                          Hash previousHash, 
                          int difficulty,
                          BigDecimal miningReward,
                          String minerAddress) {
        
        log.info("MiningDomainService: Starting mineBlock with {} transactions, difficulty: {}, reward: {}", 
                pendingTransactions.size(), difficulty, miningReward);
        
        try {
            log.info("Creating new block with previous hash: {}", previousHash != null ? previousHash.getValue() : "null");
            Block newBlock;
            try {
                newBlock = Block.create(previousHash);
                log.info("Block created successfully");
            } catch (Exception e) {
                log.error("Error creating block", e);
                throw new RuntimeException("Failed to create block: " + e.getMessage(), e);
            }
            
            log.info("Adding {} pending transactions to block", pendingTransactions.size());
            for (Transaction transaction : pendingTransactions) {
                try {
                    if (transaction.isValid()) {
                        log.info("Adding valid transaction: {}", transaction.getId());
                        newBlock.addTransaction(transaction);
                    } else {
                        log.warn("Skipping invalid transaction: {}", transaction.getId());
                    }
                } catch (Exception e) {
                    log.error("Error adding transaction: {}", transaction.getId(), e);
                    throw new RuntimeException("Failed to add transaction: " + e.getMessage(), e);
                }
            }
            
            if (minerAddress != null && !minerAddress.isEmpty()) {
                log.info("Creating mining reward transaction for address: {}", minerAddress);
                try {
                    Transaction rewardTransaction = createMiningRewardTransaction(
                        Address.of(minerAddress), 
                        Amount.of(miningReward)
                    );
                    newBlock.addTransaction(rewardTransaction);
                    log.info("Mining reward transaction added");
                } catch (Exception e) {
                    log.error("Error creating mining reward transaction", e);
                    throw new RuntimeException("Failed to create mining reward transaction: " + e.getMessage(), e);
                }
            }
            
            log.info("Starting proof-of-work with difficulty: {}", difficulty);
            performProofOfWork(newBlock, difficulty);
            log.info("Proof-of-work completed");
            
            log.info("Marking block as mined");
            newBlock.markAsMined(difficulty);
            log.info("Block marked as mined successfully");
            
            return newBlock;
        } catch (Exception e) {
            log.error("Error in mineBlock", e);
            throw e;
        }
    }

    /**
     * Performs proof-of-work algorithm to find a valid nonce.
     * 
     * @param block the block to mine
     * @param difficulty the mining difficulty
     */
    private void performProofOfWork(Block block, int difficulty) {
        log.info("Starting proof-of-work with difficulty: {}", difficulty);
        int maxIterations = 1000000;
        int iterations = 0;
        
        while (!meetsDifficulty(block, difficulty) && iterations < maxIterations) {
            block.incrementNonce();
            iterations++;
            
            if (iterations % 100000 == 0) {
                log.info("Proof-of-work iteration: {}, nonce: {}", iterations, block.getNonce().getValue());
            }
        }
        
        if (iterations >= maxIterations) {
            log.warn("Proof-of-work reached maximum iterations: {}", maxIterations);
        } else {
            log.info("Proof-of-work completed in {} iterations", iterations);
        }
    }

    /**
     * Checks if the block meets the required mining difficulty.
     * 
     * @param block the block to check
     * @param difficulty the required difficulty level
     * @return true if the block meets the difficulty requirement
     */
    public boolean meetsDifficulty(Block block, int difficulty) {
        return block.meetsDifficulty(difficulty);
    }

    /**
     * Creates a mining reward transaction.
     * 
     * @param minerAddress address of the miner
     * @param rewardAmount amount of the mining reward
     * @return the mining reward transaction
     */
    private Transaction createMiningRewardTransaction(Address minerAddress, Amount rewardAmount) {
        return Transaction.create(null, minerAddress, rewardAmount);
    }

    /**
     * Validates if a block can be mined with the given parameters.
     * 
     * @param pendingTransactions list of pending transactions
     * @param difficulty mining difficulty
     * @param miningReward mining reward amount
     * @return true if mining is possible with given parameters
     */
    public boolean canMineBlock(List<Transaction> pendingTransactions, int difficulty, BigDecimal miningReward) {
        if (pendingTransactions == null || pendingTransactions.isEmpty()) {
            return false;
        }
        
        if (difficulty <= 0) {
            return false;
        }
        
        if (miningReward == null || miningReward.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        return true;
    }

    /**
     * Calculates the estimated mining time based on difficulty.
     * This is a simplified calculation for demonstration purposes.
     * 
     * @param difficulty the mining difficulty
     * @return estimated mining time in seconds
     */
    public long estimateMiningTime(int difficulty) {
        // Simplified calculation: 2^difficulty * 0.1 seconds
        return (long) (Math.pow(2, difficulty) * 0.1);
    }
}
