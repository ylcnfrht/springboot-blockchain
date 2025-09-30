package com.ylcnfrht.blockchain.application.exceptions;

/**
 * Application exception for blockchain-related operations.
 */
public class BlockchainApplicationException extends ApplicationException {
    
    public BlockchainApplicationException(ErrorCode errorCode, String message, String userMessage) {
        super(errorCode, message, userMessage);
    }
    
    public BlockchainApplicationException(ErrorCode errorCode, String message, String userMessage, Throwable cause) {
        super(errorCode, message, userMessage, cause);
    }
    
    public static BlockchainApplicationException blockNotFound(Long id) {
        return new BlockchainApplicationException(
            ErrorCode.BLOCK_NOT_FOUND,
            "Block not found with id: " + id,
            "Block not found"
        );
    }
    
    public static BlockchainApplicationException blockNotFound(String hash) {
        return new BlockchainApplicationException(
            ErrorCode.BLOCK_NOT_FOUND,
            "Block not found with hash: " + hash,
            "Block not found"
        );
    }
    
    public static BlockchainApplicationException miningFailed(String reason) {
        return new BlockchainApplicationException(
            ErrorCode.MINING_FAILED,
            "Failed to mine block: " + reason,
            "Failed to mine block"
        );
    }
    
    public static BlockchainApplicationException noPendingTransactions() {
        return new BlockchainApplicationException(
            ErrorCode.NO_PENDING_TRANSACTIONS,
            "No pending transactions to mine",
            "No pending transactions available"
        );
    }
    
    public static BlockchainApplicationException blockchainValidationFailed(String reason) {
        return new BlockchainApplicationException(
            ErrorCode.BLOCKCHAIN_VALIDATION_FAILED,
            "Blockchain validation failed: " + reason,
            "Blockchain validation failed"
        );
    }
    
    public static BlockchainApplicationException blockCreationFailed(String reason) {
        return new BlockchainApplicationException(
            ErrorCode.BLOCK_CREATION_FAILED,
            "Failed to create block: " + reason,
            "Failed to create block"
        );
    }
    
    public static BlockchainApplicationException statsCalculationFailed(String reason) {
        return new BlockchainApplicationException(
            ErrorCode.STATS_CALCULATION_FAILED,
            "Failed to calculate blockchain stats: " + reason,
            "Failed to calculate blockchain statistics"
        );
    }
    
    public static BlockchainApplicationException getAllBlocksFailed(String reason) {
        return new BlockchainApplicationException(
            ErrorCode.GET_ALL_BLOCKS_FAILED,
            "Failed to retrieve all blocks: " + reason,
            "Failed to retrieve blocks"
        );
    }
    
    public static BlockchainApplicationException getBlockByIdFailed(Long id, String reason) {
        return new BlockchainApplicationException(
            ErrorCode.GET_BLOCK_BY_ID_FAILED,
            "Failed to retrieve block with id " + id + ": " + reason,
            "Failed to retrieve block"
        );
    }
    
    public static BlockchainApplicationException getBlockByHashFailed(String hash, String reason) {
        return new BlockchainApplicationException(
            ErrorCode.GET_BLOCK_BY_HASH_FAILED,
            "Failed to retrieve block with hash " + hash + ": " + reason,
            "Failed to retrieve block"
        );
    }
    
    public static BlockchainApplicationException getLatestBlockFailed(String reason) {
        return new BlockchainApplicationException(
            ErrorCode.GET_LATEST_BLOCK_FAILED,
            "Failed to retrieve latest block: " + reason,
            "Failed to retrieve latest block"
        );
    }
}
