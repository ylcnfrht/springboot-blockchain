package com.ylcnfrht.blockchain.infrastructure.web.result;

/**
 * Enum for API error codes.
 * Provides centralized error code management for API responses.
 */
public enum ApiErrorCode {
    
    // Blockchain related errors
    BLOCKCHAIN_ERROR("BLOCKCHAIN_ERROR"),
    BLOCK_NOT_FOUND("BLOCK_NOT_FOUND"),
    NO_BLOCKS_FOUND("NO_BLOCKS_FOUND"),
    MINING_ERROR("MINING_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    STATS_ERROR("STATS_ERROR"),
    
    // Wallet related errors
    WALLET_ERROR("WALLET_ERROR"),
    WALLET_NOT_FOUND("WALLET_NOT_FOUND"),
    WALLET_CREATION_ERROR("WALLET_CREATION_ERROR"),
    WALLET_UPDATE_ERROR("WALLET_UPDATE_ERROR"),
    WALLET_DELETION_ERROR("WALLET_DELETION_ERROR"),
    BALANCE_ERROR("BALANCE_ERROR"),
    
    // Transaction related errors
    TRANSACTION_ERROR("TRANSACTION_ERROR"),
    TRANSACTION_NOT_FOUND("TRANSACTION_NOT_FOUND"),
    TRANSACTION_CREATION_ERROR("TRANSACTION_CREATION_ERROR"),
    TRANSACTION_UPDATE_ERROR("TRANSACTION_UPDATE_ERROR"),
    TRANSACTION_DELETION_ERROR("TRANSACTION_DELETION_ERROR"),
    TRANSACTION_SIGNING_ERROR("TRANSACTION_SIGNING_ERROR"),
    
    // General errors
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR"),
    VALIDATION_FAILED("VALIDATION_FAILED"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    BAD_REQUEST("BAD_REQUEST");
    
    private final String code;
    
    ApiErrorCode(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return code;
    }
}
