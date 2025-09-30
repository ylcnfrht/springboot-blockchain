package com.ylcnfrht.blockchain.domain.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

/**
 * Domain service responsible for wallet balance calculations and updates.
 * Contains business logic for balance computation that was previously in Application layer.
 */
@Service
public class WalletBalanceDomainService {

    /**
     * Calculates the confirmed balance for a given address based on mined transactions.
     * 
     * @param address the wallet address to calculate balance for
     * @param transactions all transactions involving this address
     * @return the confirmed balance
     */
    public Balance calculateConfirmedBalance(Address address, List<Transaction> transactions) {
        List<Transaction> minedTransactions = transactions.stream()
            .filter(Transaction::isMined)
            .toList();

        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : minedTransactions) {
            // Subtract amount if this address is the sender
            if (transaction.getFromAddress() != null && 
                address.equals(transaction.getFromAddress())) {
                balance = balance.subtract(transaction.getAmount().getValue());
            }
            
            // Add amount if this address is the receiver
            if (transaction.getToAddress() != null && 
                address.equals(transaction.getToAddress())) {
                balance = balance.add(transaction.getAmount().getValue());
            }
        }

        return Balance.of(balance);
    }

    /**
     * Calculates the pending balance for a given address based on pending transactions.
     * 
     * @param address the wallet address to calculate pending balance for
     * @param pendingTransactions all pending transactions involving this address
     * @return the pending balance
     */
    public Balance calculatePendingBalance(Address address, List<Transaction> pendingTransactions) {
        BigDecimal pendingBalance = BigDecimal.ZERO;

        for (Transaction transaction : pendingTransactions) {
            // Subtract amount if this address is the sender
            if (transaction.getFromAddress() != null && 
                address.equals(transaction.getFromAddress())) {
                pendingBalance = pendingBalance.subtract(transaction.getAmount().getValue());
            }
            
            // Add amount if this address is the receiver
            if (transaction.getToAddress() != null && 
                address.equals(transaction.getToAddress())) {
                pendingBalance = pendingBalance.add(transaction.getAmount().getValue());
            }
        }

        // Pending balance can be negative, so we need to handle this case
        if (pendingBalance.compareTo(BigDecimal.ZERO) < 0) {
            return Balance.of(BigDecimal.ZERO);
        }
        return Balance.of(pendingBalance);
    }

    /**
     * Updates wallet balances after mining based on all transactions.
     * 
     * @param wallets all active wallets to update
     * @param allTransactions all transactions (including newly mined ones)
     */
    public void updateWalletBalancesAfterMining(List<Wallet> wallets, List<Transaction> allTransactions) {
        for (Wallet wallet : wallets) {
            try {
                // Filter transactions for this specific wallet
                List<Transaction> walletTransactions = allTransactions.stream()
                    .filter(tx -> (tx.getFromAddress() != null && wallet.getAddress().equals(tx.getFromAddress())) ||
                                 (tx.getToAddress() != null && wallet.getAddress().equals(tx.getToAddress())))
                    .toList();
                
                Balance newBalance = calculateConfirmedBalance(wallet.getAddress(), walletTransactions);
                wallet.setBalance(newBalance);
            } catch (Exception e) {
                // Log error but continue with other wallets
                System.err.println("Error updating balance for wallet " + wallet.getAddress().getValue() + ": " + e.getMessage());
                // Set balance to zero if there's an error
                wallet.setBalance(Balance.of(BigDecimal.ZERO));
            }
        }
    }

    /**
     * Checks if a wallet has sufficient balance for a transaction.
     * 
     * @param address the wallet address to check
     * @param amount the amount to check against
     * @param transactions all transactions involving this address
     * @return true if the wallet has sufficient balance
     */
    public boolean hasEnoughBalance(Address address, BigDecimal amount, List<Transaction> transactions) {
        Balance currentBalance = calculateConfirmedBalance(address, transactions);
        return currentBalance.getValue().compareTo(amount) >= 0;
    }
}
