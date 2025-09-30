package com.ylcnfrht.blockchain.domain.services;

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
    public boolean isChainValid(List<Block> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return true; // Empty blockchain is considered valid
        }

        // Validate each block individually
        for (Block block : blocks) {
            if (!isBlockValid(block)) {
                return false;
            }
        }

        // Validate chain connections
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);
            
            if (!isBlockConnectionValid(currentBlock, previousBlock)) {
                return false;
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

        // Check if block has valid hash
        if (block.getHash() == null) {
            return false;
        }

        // Check if block is properly mined
        if (!block.isMined()) {
            return false;
        }

        // Check if block has valid timestamp
        if (block.getTimestamp() == null) {
            return false;
        }

        // Check if block has valid nonce
        if (block.getNonce() == null) {
            return false;
        }

        // Validate block's internal state
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

        // Check if current block's previous hash matches previous block's hash
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

        // Validate the new block itself
        if (!isBlockValid(newBlock)) {
            return false;
        }

        // If this is the first block (genesis block)
        if (latestBlock == null) {
            return newBlock.getPreviousHash() == null;
        }

        // Validate connection with the latest block
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

        // Validate each block
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (!isBlockValid(block)) {
                result.setValid(false);
                result.setMessage("Invalid block at index " + i + " with ID " + block.getId());
                return result;
            }
        }

        // Validate chain connections
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
