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
import com.ylcnfrht.blockchain.domain.services.WalletBalanceDomainService;
import com.ylcnfrht.blockchain.domain.services.WalletSecurityDomainService;
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
  private final WalletBalanceDomainService walletBalanceDomainService;
  private final WalletSecurityDomainService walletSecurityDomainService;

  public List<WalletResponseDto> getAllWallets() {
    log.info("Getting all wallets from repository");
    try {
      List<Wallet> wallets = walletRepository.findByActiveTrue();
      log.info("Found {} wallets", wallets.size());
      return wallets.stream()
          .map(walletDtoMapper::toWalletResponseDto)
          .toList();
    } catch (Exception e) {
      log.error("Error getting wallets", e);
      throw e;
    }
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
    Address address = Address.of(request.getAddress());
    
    // Validate address uniqueness using domain service
    walletSecurityDomainService.validateAddressUniqueness(address, 
        addr -> walletRepository.existsByAddress(addr.getValue()));
    
    // Validate wallet creation parameters using domain service
    walletSecurityDomainService.validateWalletCreation(
        address, 
        request.getPublicKey(), 
        request.getPrivateKey()
    );

    Wallet wallet = Wallet.create(
        address,
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
            // Validate key rotation using domain service
            walletSecurityDomainService.validateKeyRotation(
                request.getPrivateKey(), 
                existingWallet.getPublicKey()
            );
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
    try {
      log.info("Getting wallet balance for address: {}", address);
      Address walletAddress = Address.of(address);
      log.info("Created wallet address: {}", walletAddress.getValue());
      
      List<Transaction> transactions = transactionRepository.findByAddress(walletAddress);
      log.info("Found {} transactions for address", transactions.size());
      
      List<Transaction> pendingTransactions = transactionRepository.findPending()
          .stream()
          .filter(tx -> (tx.getFromAddress() != null && address.equals(tx.getFromAddress().getValue()))
              || (tx.getToAddress() != null && address.equals(tx.getToAddress().getValue())))
          .toList();
      log.info("Found {} pending transactions for address", pendingTransactions.size());

      // Use domain service for balance calculations
      Balance confirmedBalance = walletBalanceDomainService.calculateConfirmedBalance(walletAddress, transactions);
      log.info("Calculated confirmed balance: {}", confirmedBalance.getValue());
      
      Balance pendingBalance = walletBalanceDomainService.calculatePendingBalance(walletAddress, pendingTransactions);
      log.info("Calculated pending balance: {}", pendingBalance.getValue());

      return walletDtoMapper.toWalletBalanceResponseDto(address, confirmedBalance.getValue(), pendingBalance.getValue());
    } catch (Exception e) {
      log.error("Error getting wallet balance for address: {}", address, e);
      throw e;
    }
  }


  public boolean hasEnoughBalance(String address, BigDecimal amount) {
    Address walletAddress = Address.of(address);
    List<Transaction> transactions = transactionRepository.findByAddress(walletAddress);
    return walletBalanceDomainService.hasEnoughBalance(walletAddress, amount, transactions);
  }

  public List<Transaction> getWalletTransactionHistory(String address) {
    return transactionRepository.findByAddress(Address.of(address));
  }

  public void updateWalletBalancesAfterMining() {
    List<Wallet> wallets = walletRepository.findByActiveTrue();
    List<Transaction> allTransactions = transactionRepository.findAll();

    // Use domain service for balance updates
    walletBalanceDomainService.updateWalletBalancesAfterMining(wallets, allTransactions);
    
    // Save updated wallets
    for (Wallet wallet : wallets) {
      walletRepository.save(wallet);
      log.info("Updated wallet balance for {}: {}", wallet.getAddress().getValue(), wallet.getBalance().getValue());
    }
  }

}