package com.ylcnfrht.blockchain.application.wallet;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.domain.blockchain.Transaction;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.infrastructure.persistence.TransactionRepository;
import com.ylcnfrht.blockchain.infrastructure.persistence.WalletRepository;
import com.ylcnfrht.blockchain.infrastructure.web.dto.request.CreateWalletRequest;
import com.ylcnfrht.blockchain.infrastructure.web.dto.response.WalletBalanceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletApplicationService {

  private final WalletRepository walletRepository;
  private final TransactionRepository transactionRepository;

  public List<Wallet> getAllWallets() {
    return walletRepository.findByActiveTrue();
  }

  public Optional<Wallet> getWalletById(Long id) {
    return walletRepository.findById(id);
  }

  public Optional<Wallet> getWalletByAddress(String address) {
    return walletRepository.findByAddress(address);
  }

  public Wallet createWallet(CreateWalletRequest request) {
    if (walletRepository.existsByAddress(request.getAddress())) {
      throw new IllegalArgumentException("Wallet with this address already exists");
    }

    Wallet wallet = Wallet.builder()
        .address(request.getAddress())
        .publicKey(request.getPublicKey())
        .privateKey(request.getPrivateKey())
        .balance(BigDecimal.ZERO)
        .createdAt(LocalDateTime.now())
        .active(true)
        .build();

    Wallet savedWallet = walletRepository.save(wallet);
    log.info("Created new wallet with address: {}", savedWallet.getAddress());
    return savedWallet;
  }

  public Optional<Wallet> updateWallet(Long id, CreateWalletRequest request) {
    return walletRepository.findById(id)
        .map(existingWallet -> {
          if (request.getPrivateKey() != null) {
            existingWallet.setPrivateKey(request.getPrivateKey());
          }

          Wallet updatedWallet = walletRepository.save(existingWallet);
          log.info("Updated wallet with id: {}", id);
          return updatedWallet;
        });
  }

  public boolean deleteWallet(Long id) {
    return walletRepository.findById(id)
        .map(wallet -> {
          wallet.setActive(false);
          walletRepository.save(wallet);
          log.info("Deactivated wallet with id: {}", id);
          return true;
        })
        .orElse(false);
  }

  public Optional<Wallet> deactivateWallet(Long id) {
    return walletRepository.findById(id)
        .map(wallet -> {
          wallet.setActive(false);
          Wallet deactivatedWallet = walletRepository.save(wallet);
          log.info("Deactivated wallet with id: {}", id);
          return deactivatedWallet;
        });
  }

  public WalletBalanceResponse getWalletBalance(String address) {
    BigDecimal confirmedBalance = calculateConfirmedBalance(address);
    BigDecimal pendingBalance = calculatePendingBalance(address);

    return WalletBalanceResponse.builder()
        .address(address)
        .balance(confirmedBalance)
        .pendingBalance(pendingBalance)
        .build();
  }

  private BigDecimal calculateConfirmedBalance(String address) {
    List<Transaction> minedTransactions = transactionRepository.findAll()
        .stream()
        .filter(tx -> tx.getBlock() != null)
        .filter(tx -> address.equals(tx.getFromAddress()) || address.equals(tx.getToAddress()))
        .toList();

    log.info("Found {} mined transactions for address {}", minedTransactions.size(), address);

    BigDecimal balance = BigDecimal.ZERO;

    for (Transaction transaction : minedTransactions) {
      log.info("Processing transaction: from={}, to={}, amount={}",
          transaction.getFromAddress(), transaction.getToAddress(), transaction.getAmount());

      if (address.equals(transaction.getFromAddress())) {
        balance = balance.subtract(transaction.getAmount());
        log.info("Subtracted {} from balance, new balance: {}", transaction.getAmount(), balance);
      }
      if (address.equals(transaction.getToAddress())) {
        balance = balance.add(transaction.getAmount());
        log.info("Added {} to balance, new balance: {}", transaction.getAmount(), balance);
      }
    }

    log.info("Final confirmed balance for {}: {}", address, balance);
    return balance;
  }

  private BigDecimal calculatePendingBalance(String address) {
    List<Transaction> pendingTransactions = transactionRepository.findByBlockIsNull()
        .stream()
        .filter(tx -> address.equals(tx.getFromAddress()) || address.equals(tx.getToAddress()))
        .toList();

    BigDecimal pendingBalance = BigDecimal.ZERO;

    for (Transaction transaction : pendingTransactions) {
      if (address.equals(transaction.getFromAddress())) {
        pendingBalance = pendingBalance.subtract(transaction.getAmount());
      }
      if (address.equals(transaction.getToAddress())) {
        pendingBalance = pendingBalance.add(transaction.getAmount());
      }
    }

    return pendingBalance;
  }

  public boolean hasEnoughBalance(String address, BigDecimal amount) {
    BigDecimal currentBalance = calculateConfirmedBalance(address);
    return currentBalance.compareTo(amount) >= 0;
  }

  public List<Transaction> getWalletTransactionHistory(String address) {
    return transactionRepository.findByAddress(address);
  }

  public void updateWalletBalancesAfterMining() {
    List<Wallet> wallets = walletRepository.findByActiveTrue();

    for (Wallet wallet : wallets) {
      BigDecimal currentBalance = calculateConfirmedBalance(wallet.getAddress());
      wallet.setBalance(currentBalance);
      walletRepository.save(wallet);
      log.info("Updated wallet balance for {}: {}", wallet.getAddress(), currentBalance);
    }
  }
}