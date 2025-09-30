package com.ylcnfrht.blockchain.domain.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance;
import com.ylcnfrht.blockchain.domain.common.DomainException;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

/**
 * Domain service responsible for transaction validation operations.
 * Contains business logic for validating transactions before creation and processing.
 */
@Service
public class TransactionValidationDomainService {

    /**
     * Validates if a transaction can be created with the given parameters.
     * 
     * @param fromAddress sender address (can be null for mining rewards)
     * @param toAddress receiver address
     * @param amount transaction amount
     * @param currentBalance current balance of the sender (if applicable)
     * @throws DomainException if validation fails
     */
    public void validateTransactionCreation(Address fromAddress, 
                                          Address toAddress, 
                                          Amount amount,
                                          Balance currentBalance) {
        
        if (toAddress == null) {
            throw new DomainException("Transaction receiver address cannot be null");
        }

        if (amount == null) {
            throw new DomainException("Transaction amount cannot be null");
        }

        if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amount must be positive");
        }

        if (fromAddress != null) {
            if (fromAddress.equals(toAddress)) {
                throw new DomainException("Sender and receiver addresses cannot be the same");
            }

            if (currentBalance == null) {
                throw new DomainException("Current balance is required for transaction validation");
            }

            if (!hasEnoughBalance(currentBalance, amount)) {
                throw new DomainException("Insufficient balance for transaction");
            }
        }
    }

    /**
     * Checks if a wallet has sufficient balance for a transaction.
     * 
     * @param currentBalance current balance of the wallet
     * @param amount amount to be transferred
     * @return true if the wallet has sufficient balance
     */
    public boolean hasEnoughBalance(Balance currentBalance, Amount amount) {
        if (currentBalance == null || amount == null) {
            return false;
        }
        
        return currentBalance.getValue().compareTo(amount.getValue()) >= 0;
    }

    /**
     * Validates if a transaction can be updated.
     * 
     * @param transaction the transaction to validate for update
     * @throws DomainException if the transaction cannot be updated
     */
    public void validateTransactionUpdate(Transaction transaction) {
        if (transaction == null) {
            throw new DomainException("Transaction cannot be null");
        }

        if (transaction.isMined()) {
            throw new DomainException("Cannot update a mined transaction");
        }

        if (!transaction.isValid()) {
            throw new DomainException("Cannot update an invalid transaction");
        }
    }

    /**
     * Validates if a transaction can be deleted.
     * 
     * @param transaction the transaction to validate for deletion
     * @throws DomainException if the transaction cannot be deleted
     */
    public void validateTransactionDeletion(Transaction transaction) {
        if (transaction == null) {
            throw new DomainException("Transaction cannot be null");
        }

        if (transaction.isMined()) {
            throw new DomainException("Cannot delete a mined transaction");
        }
    }

    /**
     * Validates if a transaction can be included in a block.
     * 
     * @param transaction the transaction to validate
     * @throws DomainException if the transaction cannot be included in a block
     */
    public void validateTransactionForBlock(Transaction transaction) {
        if (transaction == null) {
            throw new DomainException("Transaction cannot be null");
        }

        if (transaction.isMined()) {
            throw new DomainException("Transaction is already mined");
        }

        if (!transaction.isValid()) {
            throw new DomainException("Transaction is not valid");
        }
    }

    /**
     * Validates the signature of a transaction.
     * 
     * @param transaction the transaction to validate
     * @return true if the signature is valid, false otherwise
     */
    public boolean validateTransactionSignature(Transaction transaction) {
        if (transaction == null) {
            return false;
        }

        if (transaction.getFromAddress() == null) {
            return true;
        }

        return transaction.getSignature() != null && transaction.isValid();
    }

    /**
     * Validates if a transaction amount is within acceptable limits.
     * 
     * @param amount the amount to validate
     * @param maxAmount maximum allowed amount
     * @throws DomainException if the amount exceeds limits
     */
    public void validateTransactionAmount(Amount amount, BigDecimal maxAmount) {
        if (amount == null) {
            throw new DomainException("Transaction amount cannot be null");
        }

        if (maxAmount != null && amount.getValue().compareTo(maxAmount) > 0) {
            throw new DomainException("Transaction amount exceeds maximum allowed limit");
        }
    }

    /**
     * Validates if an address is valid for transaction purposes.
     * 
     * @param address the address to validate
     * @param isRequired whether the address is required
     * @throws DomainException if the address is invalid
     */
    public void validateAddress(Address address, boolean isRequired) {
        if (isRequired && address == null) {
            throw new DomainException("Address is required");
        }

        if (address != null && !isValidAddressFormat(address)) {
            throw new DomainException("Invalid address format");
        }
    }

    /**
     * Checks if an address has a valid format.
     * This is a simplified validation - in a real blockchain, this would be more complex.
     * 
     * @param address the address to validate
     * @return true if the address format is valid
     */
    private boolean isValidAddressFormat(Address address) {
        if (address == null || address.getValue() == null) {
            return false;
        }

        String addressValue = address.getValue();
        
        return !addressValue.trim().isEmpty() && 
               addressValue.length() >= 10 && 
               addressValue.length() <= 100;
    }
}
