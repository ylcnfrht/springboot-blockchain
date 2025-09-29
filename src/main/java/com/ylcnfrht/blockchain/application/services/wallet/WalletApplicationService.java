package com.ylcnfrht.blockchain.application.services.wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.dtos.request.CreateWalletRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateWalletResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletBalanceResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletResponseDto;
import com.ylcnfrht.blockchain.application.mappers.WalletDtoMapper;
import com.ylcnfrht.blockchain.application.ports.WalletService;
import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.TransactionRepositoryPort;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.domain.wallet.WalletRepositoryPort;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WalletApplicationService implements WalletService {

  private final WalletRepositoryPort walletRepository;
  private final TransactionRepositoryPort transactionRepository;
  private final WalletDtoMapper walletDtoMapper;

  public List<WalletResponseDto> getAllWallets() {
    return walletRepository.findByActiveTrue().stream()
        .map(walletDtoMapper::toWalletResponseDto)
        .toList();
  }

  public Optional<WalletResponseDto> getWalletById(Long id) {
    return walletRepository.findById(id)
        .map(walletDtoMapper::toWalletResponseDto);
  }

  public Optional<WalletResponseDto> getWalletByAddress(String address) {
    return walletRepository.findByAddress(address)
        .map(walletDtoMapper::toWalletResponseDto);
  }

  public CreateWalletResponseDto createWallet(CreateWalletRequestDto request) {
    if (walletRepository.existsByAddress(request.getAddress())) {
      throw new IllegalArgumentException("Wallet with this address already exists");
    }

    Wallet wallet = Wallet.create(
        Address.of(request.getAddress()),
        request.getPublicKey(),
        request.getPrivateKey()
    );

    Wallet savedWallet = walletRepository.save(wallet);
    log.info("Created new wallet with address: {}", savedWallet.getAddress().getValue());
    return walletDtoMapper.toCreateWalletResponseDto(savedWallet);
  }

  public Optional<WalletResponseDto> updateWallet(Long id, CreateWalletRequestDto request) {
    return walletRepository.findById(id)
        .map(existingWallet -> {
          if (request.getPrivateKey() != null) {
            existingWallet.rotateKeys(existingWallet.getPublicKey(), request.getPrivateKey());
          }

            Wallet updatedWallet = walletRepository.save(existingWallet);
            log.info("Updated wallet with id: {}", id);
            return walletDtoMapper.toWalletResponseDto(updatedWallet);
        });
  }

  public boolean deleteWallet(Long id) {
    return walletRepository.findById(id)
        .map(wallet -> {
          wallet.deactivate();
          walletRepository.save(wallet);
          log.info("Deactivated wallet with id: {}", id);
          return true;
        })
        .orElse(false);
  }

  public Optional<WalletResponseDto> deactivateWallet(Long id) {
    return walletRepository.findById(id)
        .map(wallet -> {
          wallet.deactivate();
            Wallet deactivatedWallet = walletRepository.save(wallet);
            log.info("Deactivated wallet with id: {}", id);
            return walletDtoMapper.toWalletResponseDto(deactivatedWallet);
        });
  }

  public WalletBalanceResponseDto getWalletBalance(String address) {
    BigDecimal confirmedBalance = calculateConfirmedBalance(address);
    BigDecimal pendingBalance = calculatePendingBalance(address);

    return walletDtoMapper.toWalletBalanceResponseDto(address, confirmedBalance, pendingBalance);
  }

  private BigDecimal calculateConfirmedBalance(String address) {
    List<Transaction> transactions = transactionRepository
        .findByAddress(Address.of(address));
    List<Transaction> minedTransactions = transactions.stream()
        .filter(Transaction::isMined)
        .toList();

    log.info("Found {} mined transactions for address {}", minedTransactions.size(), address);

    BigDecimal balance = BigDecimal.ZERO;

    for (Transaction transaction : minedTransactions) {
      log.info("Processing transaction: from={}, to={}, amount={}",
          transaction.getFromAddress(), transaction.getToAddress(), transaction.getAmount());

      if (transaction.getFromAddress() != null && address.equals(transaction.getFromAddress().getValue())) {
        balance = balance.subtract(transaction.getAmount().getValue());
        log.info("Subtracted {} from balance, new balance: {}", transaction.getAmount(), balance);
      }
      if (address.equals(transaction.getToAddress().getValue())) {
        balance = balance.add(transaction.getAmount().getValue());
        log.info("Added {} to balance, new balance: {}", transaction.getAmount(), balance);
      }
    }

    log.info("Final confirmed balance for {}: {}", address, balance);
    return balance;
  }

  private BigDecimal calculatePendingBalance(String address) {
    List<Transaction> pendingTransactions = transactionRepository.findPending()
        .stream()
        .filter(tx -> (tx.getFromAddress() != null && address.equals(tx.getFromAddress().getValue()))
            || address.equals(tx.getToAddress().getValue()))
        .toList();

    BigDecimal pendingBalance = BigDecimal.ZERO;

    for (Transaction transaction : pendingTransactions) {
      if (transaction.getFromAddress() != null && address.equals(transaction.getFromAddress().getValue())) {
        pendingBalance = pendingBalance.subtract(transaction.getAmount().getValue());
      }
      if (address.equals(transaction.getToAddress().getValue())) {
        pendingBalance = pendingBalance.add(transaction.getAmount().getValue());
      }
    }

    return pendingBalance;
  }

  public boolean hasEnoughBalance(String address, BigDecimal amount) {
    BigDecimal currentBalance = calculateConfirmedBalance(address);
    return currentBalance.compareTo(amount) >= 0;
  }

  public List<Transaction> getWalletTransactionHistory(String address) {
    return transactionRepository.findByAddress(Address.of(address));
  }

  public void updateWalletBalancesAfterMining() {
    List<Wallet> wallets = walletRepository.findByActiveTrue();

    for (Wallet wallet : wallets) {
      BigDecimal currentBalance = calculateConfirmedBalance(wallet.getAddress().getValue());
      wallet.setBalance(Balance.of(currentBalance));
      walletRepository.save(wallet);
      log.info("Updated wallet balance for {}: {}", wallet.getAddress().getValue(), currentBalance);
    }
  }

}