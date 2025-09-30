package com.ylcnfrht.blockchain.application.exceptions;

/**
 * Application exception for wallet-related operations.
 */
public class WalletApplicationException extends ApplicationException {
    
    public WalletApplicationException(ErrorCode errorCode, String message, String userMessage) {
        super(errorCode, message, userMessage);
    }
    
    public WalletApplicationException(ErrorCode errorCode, String message, String userMessage, Throwable cause) {
        super(errorCode, message, userMessage, cause);
    }
    
    public static WalletApplicationException walletNotFound(Long id) {
        return new WalletApplicationException(
            ErrorCode.WALLET_NOT_FOUND,
            "Wallet not found with id: " + id,
            "Wallet not found"
        );
    }
    
    public static WalletApplicationException walletNotFound(String address) {
        return new WalletApplicationException(
            ErrorCode.WALLET_NOT_FOUND,
            "Wallet not found with address: " + address,
            "Wallet not found"
        );
    }
    
    public static WalletApplicationException walletCreationFailed(String reason) {
        return new WalletApplicationException(
            ErrorCode.WALLET_CREATION_FAILED,
            "Failed to create wallet: " + reason,
            "Failed to create wallet"
        );
    }
    
    public static WalletApplicationException walletUpdateFailed(Long id, String reason) {
        return new WalletApplicationException(
            ErrorCode.WALLET_UPDATE_FAILED,
            "Failed to update wallet with id " + id + ": " + reason,
            "Failed to update wallet"
        );
    }
    
    public static WalletApplicationException walletDeletionFailed(Long id, String reason) {
        return new WalletApplicationException(
            ErrorCode.WALLET_DELETION_FAILED,
            "Failed to delete wallet with id " + id + ": " + reason,
            "Failed to delete wallet"
        );
    }
    
    public static WalletApplicationException balanceCalculationFailed(String address, String reason) {
        return new WalletApplicationException(
            ErrorCode.BALANCE_CALCULATION_FAILED,
            "Failed to calculate balance for wallet " + address + ": " + reason,
            "Failed to calculate balance"
        );
    }
    
    public static WalletApplicationException addressAlreadyExists(String address) {
        return new WalletApplicationException(
            ErrorCode.ADDRESS_ALREADY_EXISTS,
            "Wallet with address " + address + " already exists",
            "Wallet address already exists"
        );
    }
    
    public static WalletApplicationException getAllWalletsFailed(String reason) {
        return new WalletApplicationException(
            ErrorCode.GET_ALL_WALLETS_FAILED,
            "Failed to retrieve all wallets: " + reason,
            "Failed to retrieve wallets"
        );
    }
    
    public static WalletApplicationException getWalletByIdFailed(Long id, String reason) {
        return new WalletApplicationException(
            ErrorCode.GET_WALLET_BY_ID_FAILED,
            "Failed to retrieve wallet with id " + id + ": " + reason,
            "Failed to retrieve wallet"
        );
    }
    
    public static WalletApplicationException getWalletByAddressFailed(String address, String reason) {
        return new WalletApplicationException(
            ErrorCode.GET_WALLET_BY_ADDRESS_FAILED,
            "Failed to retrieve wallet with address " + address + ": " + reason,
            "Failed to retrieve wallet"
        );
    }
    
    public static WalletApplicationException balanceCheckFailed(String address, String amount, String reason) {
        return new WalletApplicationException(
            ErrorCode.BALANCE_CHECK_FAILED,
            "Failed to check balance for wallet " + address + " with amount " + amount + ": " + reason,
            "Failed to check balance"
        );
    }
    
    public static WalletApplicationException transactionHistoryFailed(String address, String reason) {
        return new WalletApplicationException(
            ErrorCode.TRANSACTION_HISTORY_FAILED,
            "Failed to get transaction history for wallet " + address + ": " + reason,
            "Failed to get transaction history"
        );
    }
    
    public static WalletApplicationException balanceUpdateFailed(String reason) {
        return new WalletApplicationException(
            ErrorCode.BALANCE_UPDATE_FAILED,
            "Failed to update wallet balances after mining: " + reason,
            "Failed to update wallet balances"
        );
    }
}
