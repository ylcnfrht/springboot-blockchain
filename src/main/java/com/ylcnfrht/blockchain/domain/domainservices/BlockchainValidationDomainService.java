package com.ylcnfrht.blockchain.domain.domainservices;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;

/**
 * Domain service responsible for blockchain validation operations.
 * Contains business logic for validating blockchain integrity and block validity.
 */
@Service
public class BlockchainValidationDomainService {
    /**
     * Validates the entire blockchain by checking all blocks and their connections.
     * 
     * @param blocks list of blocks in the blockchain (ordered by creation time)
     * @return true if the blockchain is valid, false otherwise
     */
    public boolean isChainValid(List<Block> blocks, int difficulty) {
        blocks = new java.util.ArrayList<>(blocks);
        java.util.Collections.reverse(blocks);

        if (blocks == null || blocks.isEmpty()) {
            return true;
        }

        for (int i = 0; i < blocks.size(); i++) {
            Block b = blocks.get(i);
            if (b == null || b.getHash() == null || !b.isMined()) {
                return false;
            }
            if (!b.meetsDifficulty(difficulty)) {
                return false;
            }
            if (i > 0) {
                Block prev = blocks.get(i - 1);
                if (!isBlockConnectionValid(b, prev)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Validates a single block.
     * 
     * @param block the block to validate
     * @return true if the block is valid, false otherwise
     */
    public boolean isBlockValid(Block block) {
        if (block == null) {
            return false;
        }

        if (block.getHash() == null) {
            return false;
        }

        if (!block.isMined()) {
            return false;
        }

        if (block.getTimestamp() == null) {
            return false;
        }

        if (block.getNonce() == null) {
            return false;
        }

        return block.isValid();
    }

    /**
     * Validates the connection between two consecutive blocks.
     * 
     * @param currentBlock the current block
     * @param previousBlock the previous block
     * @return true if the connection is valid, false otherwise
     */
    public boolean isBlockConnectionValid(Block currentBlock, Block previousBlock) {
        if (currentBlock == null || previousBlock == null) {
            return false;
        }

        Hash currentPreviousHash = currentBlock.getPreviousHash();
        Hash previousBlockHash = previousBlock.getHash();

        if (currentPreviousHash == null || previousBlockHash == null) {
            return false;
        }

        return currentPreviousHash.equals(previousBlockHash);
    }

    /**
     * Validates if a new block can be added to the blockchain.
     * 
     * @param newBlock the block to be added
     * @param latestBlock the current latest block in the chain
     * @return true if the block can be added, false otherwise
     */
    public boolean canAddBlock(Block newBlock, Block latestBlock) {
        if (newBlock == null) {
            return false;
        }

        if (!isBlockValid(newBlock)) {
            return false;
        }

        if (latestBlock == null) {
            return newBlock.getPreviousHash() == null;
        }

        return isBlockConnectionValid(newBlock, latestBlock);
    }

    /**
     * Validates the blockchain structure and returns detailed validation results.
     * 
     * @param blocks list of blocks in the blockchain
     * @return validation result with detailed information
     */
    public BlockchainValidationResult validateBlockchainDetailed(List<Block> blocks) {
        BlockchainValidationResult result = new BlockchainValidationResult();
        
        if (blocks == null || blocks.isEmpty()) {
            result.setValid(true);
            result.setMessage("Empty blockchain is valid");
            return result;
        }

        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (!isBlockValid(block)) {
                result.setValid(false);
                result.setMessage("Invalid block at index " + i + " with ID " + block.getId());
                return result;
            }
        }

        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);
            
            if (!isBlockConnectionValid(currentBlock, previousBlock)) {
                result.setValid(false);
                result.setMessage("Invalid connection between blocks at indices " + (i-1) + " and " + i);
                return result;
            }
        }

        result.setValid(true);
        result.setMessage("Blockchain is valid with " + blocks.size() + " blocks");
        return result;
    }

    /**
     * Inner class to hold detailed validation results.
     */
    public static class BlockchainValidationResult {
        private boolean valid;
        private String message;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
