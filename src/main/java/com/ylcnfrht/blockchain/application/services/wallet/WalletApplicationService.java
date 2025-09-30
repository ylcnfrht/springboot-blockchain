package com.ylcnfrht.blockchain.application.services.wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.dtos.request.CreateWalletRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateWalletResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.KeyPairResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletBalanceResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.WalletResponseDto;
import com.ylcnfrht.blockchain.application.exceptions.WalletApplicationException;
import com.ylcnfrht.blockchain.application.mappers.WalletDtoMapper;
import com.ylcnfrht.blockchain.application.ports.WalletService;
import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.domainservices.WalletBalanceDomainService;
import com.ylcnfrht.blockchain.domain.domainservices.WalletSecurityDomainService;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.TransactionRepositoryPort;
import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.domain.wallet.WalletRepositoryPort;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;
import com.ylcnfrht.blockchain.infrastructure.crypto.KeyPairGenerator;

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
  private final KeyPairGenerator keyPairGenerator;

  public List<WalletResponseDto> getAllWallets() {
    log.info("Getting all wallets from repository");
    try {
      List<Wallet> wallets = walletRepository.findAll();
      log.info("Successfully retrieved {} wallets", wallets.size());
      return wallets.stream()
          .map(walletDtoMapper::toWalletResponseDto)
          .toList();
    } catch (Exception e) {
      log.error("Error getting all wallets", e);
      throw WalletApplicationException.getAllWalletsFailed(e.getMessage());
    }
  }

  public Optional<WalletResponseDto> getWalletById(Long id) {
    log.info("Getting wallet by id: {}", id);
    try {
      Optional<Wallet> walletOpt = walletRepository.findById(Id.of(id));
      if (walletOpt.isPresent()) {
        log.info("Successfully retrieved wallet with id: {}", id);
        return walletOpt.map(walletDtoMapper::toWalletResponseDto);
      } else {
        log.warn("Wallet not found with id: {}", id);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting wallet by id: {}", id, e);
      throw WalletApplicationException.getWalletByIdFailed(id, e.getMessage());
    }
  }

  public Optional<WalletResponseDto> getWalletByAddress(String address) {
    log.info("Getting wallet by address: {}", address);
    try {
      Optional<Wallet> walletOpt = walletRepository.findByAddress(address);
      if (walletOpt.isPresent()) {
        log.info("Successfully retrieved wallet with address: {}", address);
        return walletOpt.map(walletDtoMapper::toWalletResponseDto);
      } else {
        log.warn("Wallet not found with address: {}", address);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting wallet by address: {}", address, e);
      throw WalletApplicationException.getWalletByAddressFailed(address, e.getMessage());
    }
  }

  public CreateWalletResponseDto createWallet(CreateWalletRequestDto request) {
    log.info("Creating wallet with address: {}", request.getAddress());
    try {
      Address address = Address.of(request.getAddress());
      
      walletSecurityDomainService.validateAddressUniqueness(address, 
          addr -> walletRepository.existsByAddress(addr.getValue()));
      
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
      log.info("Successfully created wallet with address: {}", savedWallet.getAddress().getValue());
      return walletDtoMapper.toCreateWalletResponseDto(savedWallet);
    } catch (Exception e) {
      log.error("Error creating wallet with address: {}", request.getAddress(), e);
      throw WalletApplicationException.walletCreationFailed(e.getMessage());
    }
  }

  public Optional<WalletResponseDto> updateWallet(Long id, CreateWalletRequestDto request) {
    log.info("Updating wallet with id: {}", id);
    try {
      return walletRepository.findById(Id.of(id))
          .map(existingWallet -> {
            try {
              if (request.getPrivateKey() != null) {
                walletSecurityDomainService.validateKeyRotation(
                    request.getPrivateKey(), 
                    existingWallet.getPublicKey()
                );
                existingWallet.rotateKeys(existingWallet.getPublicKey(), request.getPrivateKey());
              }

              Wallet updatedWallet = walletRepository.save(existingWallet);
              log.info("Successfully updated wallet with id: {}", id);
              return walletDtoMapper.toWalletResponseDto(updatedWallet);
            } catch (Exception e) {
              log.error("Error updating wallet with id: {}", id, e);
              throw WalletApplicationException.walletUpdateFailed(id, e.getMessage());
            }
          });
    } catch (Exception e) {
      log.error("Error finding wallet for update with id: {}", id, e);
      throw WalletApplicationException.walletUpdateFailed(id, e.getMessage());
    }
  }

  public boolean deleteWallet(Long id) {
    log.info("Deleting wallet with id: {}", id);
    try {
      return walletRepository.findById(Id.of(id))
          .map(wallet -> {
            try {
              wallet.deactivate();
              walletRepository.save(wallet);
              log.info("Successfully deactivated wallet with id: {}", id);
              return true;
            } catch (Exception e) {
              log.error("Error deactivating wallet with id: {}", id, e);
              throw WalletApplicationException.walletDeletionFailed(id, e.getMessage());
            }
          })
          .orElse(false);
    } catch (Exception e) {
      log.error("Error finding wallet for deletion with id: {}", id, e);
      throw WalletApplicationException.walletDeletionFailed(id, e.getMessage());
    }
  }

  public Optional<WalletResponseDto> deactivateWallet(Long id) {
    log.info("Deactivating wallet with id: {}", id);
    try {
      return walletRepository.findById(Id.of(id))
          .map(wallet -> {
            try {
              wallet.deactivate();
              Wallet deactivatedWallet = walletRepository.save(wallet);
              log.info("Successfully deactivated wallet with id: {}", id);
              return walletDtoMapper.toWalletResponseDto(deactivatedWallet);
            } catch (Exception e) {
              log.error("Error deactivating wallet with id: {}", id, e);
              throw WalletApplicationException.walletDeletionFailed(id, e.getMessage());
            }
          });
    } catch (Exception e) {
      log.error("Error finding wallet for deactivation with id: {}", id, e);
      throw WalletApplicationException.walletDeletionFailed(id, e.getMessage());
    }
  }

  public WalletBalanceResponseDto getWalletBalance(String address) {
    log.info("Getting wallet balance for address: {}", address);
    try {
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

      Balance confirmedBalance = walletBalanceDomainService.calculateConfirmedBalance(walletAddress, transactions);
      log.info("Calculated confirmed balance: {}", confirmedBalance.getValue());
      
      Balance pendingBalance = walletBalanceDomainService.calculatePendingBalance(walletAddress, pendingTransactions);
      log.info("Calculated pending balance: {}", pendingBalance.getValue());

      log.info("Successfully calculated balance for address: {}", address);
      return walletDtoMapper.toWalletBalanceResponseDto(address, confirmedBalance.getValue(), pendingBalance.getValue());
    } catch (Exception e) {
      log.error("Error getting wallet balance for address: {}", address, e);
      throw WalletApplicationException.balanceCalculationFailed(address, e.getMessage());
    }
  }


  public boolean hasEnoughBalance(String address, BigDecimal amount) {
    log.info("Checking if wallet {} has enough balance for amount: {}", address, amount);
    try {
      Address walletAddress = Address.of(address);
      List<Transaction> transactions = transactionRepository.findByAddress(walletAddress);
      boolean hasEnough = walletBalanceDomainService.hasEnoughBalance(walletAddress, amount, transactions);
      log.info("Wallet {} has enough balance: {}", address, hasEnough);
      return hasEnough;
    } catch (Exception e) {
      log.error("Error checking balance for wallet {} with amount: {}", address, amount, e);
      throw WalletApplicationException.balanceCheckFailed(address, amount.toString(), e.getMessage());
    }
  }

  public List<Transaction> getWalletTransactionHistory(String address) {
    log.info("Getting transaction history for wallet: {}", address);
    try {
      List<Transaction> transactions = transactionRepository.findByAddress(Address.of(address));
      log.info("Successfully retrieved {} transactions for wallet: {}", transactions.size(), address);
      return transactions;
    } catch (Exception e) {
      log.error("Error getting transaction history for wallet: {}", address, e);
      throw WalletApplicationException.transactionHistoryFailed(address, e.getMessage());
    }
  }

  public void updateWalletBalancesAfterMining() {
    log.info("Updating wallet balances after mining");
    try {
      List<Wallet> wallets = walletRepository.findAll();
      List<Transaction> allTransactions = transactionRepository.findAll();
      log.info("Found {} wallets and {} transactions for balance update", wallets.size(), allTransactions.size());

      walletBalanceDomainService.updateWalletBalancesAfterMining(wallets, allTransactions);
      
      for (Wallet wallet : wallets) {
        walletRepository.save(wallet);
        log.info("Updated wallet balance for {}: {}", wallet.getAddress().getValue(), wallet.getBalance().getValue());
      }
      
      log.info("Successfully updated balances for {} wallets after mining", wallets.size());
    } catch (Exception e) {
      log.error("Error updating wallet balances after mining", e);
      throw WalletApplicationException.balanceUpdateFailed(e.getMessage());
    }
  }

  @Override
  public KeyPairResponseDto generateKeyPair() {
    log.info("Generating new ECDSA key pair");
    try {
      KeyPairGenerator.KeyPairStrings keyPairStrings = keyPairGenerator.generateKeyPairStrings();
      
      log.info("Successfully generated ECDSA key pair");
      return walletDtoMapper.toKeyPairResponseDto(
          keyPairStrings,
          "ECDSA_SHA256"
      );
    } catch (Exception e) {
      log.error("Error generating key pair", e);
      throw WalletApplicationException.keyPairGenerationFailed(e.getMessage());
    }
  }

}