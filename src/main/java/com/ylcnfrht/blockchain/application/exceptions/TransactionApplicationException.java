package com.ylcnfrht.blockchain.application.exceptions;

/**
 * Application exception for transaction-related operations.
 */
public class TransactionApplicationException extends ApplicationException {
    
    public TransactionApplicationException(ErrorCode errorCode, String message, String userMessage) {
        super(errorCode, message, userMessage);
    }
    
    public TransactionApplicationException(ErrorCode errorCode, String message, String userMessage, Throwable cause) {
        super(errorCode, message, userMessage, cause);
    }
    
    public static TransactionApplicationException transactionNotFound(Long id) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_NOT_FOUND,
            "Transaction not found with id: " + id,
            "Transaction not found"
        );
    }
    
    public static TransactionApplicationException transactionNotFound(String hash) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_NOT_FOUND,
            "Transaction not found with hash: " + hash,
            "Transaction not found"
        );
    }
    
    public static TransactionApplicationException transactionCreationFailed(String reason) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_CREATION_FAILED,
            "Failed to create transaction: " + reason,
            "Failed to create transaction"
        );
    }
    
    public static TransactionApplicationException transactionUpdateFailed(Long id, String reason) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_UPDATE_FAILED,
            "Failed to update transaction with id " + id + ": " + reason,
            "Failed to update transaction"
        );
    }
    
    public static TransactionApplicationException transactionDeletionFailed(Long id, String reason) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_DELETION_FAILED,
            "Failed to delete transaction with id " + id + ": " + reason,
            "Failed to delete transaction"
        );
    }
    
    public static TransactionApplicationException invalidTransaction(String reason) {
        return new TransactionApplicationException(
            ErrorCode.INVALID_TRANSACTION,
            "Invalid transaction: " + reason,
            "Invalid transaction"
        );
    }
    
    public static TransactionApplicationException insufficientBalance(String address, String amount) {
        return new TransactionApplicationException(
            ErrorCode.INSUFFICIENT_BALANCE,
            "Insufficient balance for wallet " + address + " to transfer " + amount,
            "Insufficient balance"
        );
    }
    
    public static TransactionApplicationException getAllTransactionsFailed(String reason) {
        return new TransactionApplicationException(
            ErrorCode.GET_ALL_TRANSACTIONS_FAILED,
            "Failed to retrieve all transactions: " + reason,
            "Failed to retrieve transactions"
        );
    }
    
    public static TransactionApplicationException getTransactionByIdFailed(Long id, String reason) {
        return new TransactionApplicationException(
            ErrorCode.GET_TRANSACTION_BY_ID_FAILED,
            "Failed to retrieve transaction with id " + id + ": " + reason,
            "Failed to retrieve transaction"
        );
    }
    
    public static TransactionApplicationException getTransactionByHashFailed(String hash, String reason) {
        return new TransactionApplicationException(
            ErrorCode.GET_TRANSACTION_BY_HASH_FAILED,
            "Failed to retrieve transaction with hash " + hash + ": " + reason,
            "Failed to retrieve transaction"
        );
    }
    
    public static TransactionApplicationException getTransactionsByAddressFailed(String address, String reason) {
        return new TransactionApplicationException(
            ErrorCode.GET_TRANSACTIONS_BY_ADDRESS_FAILED,
            "Failed to retrieve transactions for address " + address + ": " + reason,
            "Failed to retrieve transactions"
        );
    }
    
    public static TransactionApplicationException getPendingTransactionsFailed(String reason) {
        return new TransactionApplicationException(
            ErrorCode.GET_PENDING_TRANSACTIONS_FAILED,
            "Failed to retrieve pending transactions: " + reason,
            "Failed to retrieve pending transactions"
        );
    }
    
    public static TransactionApplicationException transactionAlreadySigned(Long id) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_ALREADY_SIGNED,
            "Transaction with id " + id + " is already signed",
            "Transaction is already signed"
        );
    }
    
    public static TransactionApplicationException transactionSigningFailed(Long id, String reason) {
        return new TransactionApplicationException(
            ErrorCode.TRANSACTION_SIGNING_FAILED,
            "Failed to sign transaction with id " + id + ": " + reason,
            "Failed to sign transaction"
        );
    }
}
