package com.ylcnfrht.blockchain.application.services.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.dtos.request.CreateTransactionRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;
import com.ylcnfrht.blockchain.application.exceptions.TransactionApplicationException;
import com.ylcnfrht.blockchain.application.mappers.TransactionDtoMapper;
import com.ylcnfrht.blockchain.application.ports.TransactionService;
import com.ylcnfrht.blockchain.application.services.wallet.WalletApplicationService;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.services.TransactionValidationDomainService;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.TransactionRepositoryPort;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionApplicationService implements TransactionService {

  private final TransactionRepositoryPort transactionRepository;
  private final WalletApplicationService walletApplicationService;
  private final TransactionDtoMapper transactionDtoMapper;
  private final TransactionValidationDomainService transactionValidationDomainService;

  public List<TransactionResponseDto> getAllTransactions() {
    log.info("Getting all transactions from repository");
    try {
      List<Transaction> transactions = transactionRepository.findAll();
      log.info("Successfully retrieved {} transactions", transactions.size());
      return transactions.stream()
          .map(transactionDtoMapper::toTransactionResponseDto)
          .toList();
    } catch (Exception e) {
      log.error("Error getting all transactions", e);
      throw TransactionApplicationException.getAllTransactionsFailed(e.getMessage());
    }
  }

  public Optional<TransactionResponseDto> getTransactionById(Long id) {
    log.info("Getting transaction by id: {}", id);
    try {
      Optional<Transaction> transactionOpt = transactionRepository.findById(Id.of(id));
      if (transactionOpt.isPresent()) {
        log.info("Successfully retrieved transaction with id: {}", id);
        return transactionOpt.map(transactionDtoMapper::toTransactionResponseDto);
      } else {
        log.warn("Transaction not found with id: {}", id);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting transaction by id: {}", id, e);
      throw TransactionApplicationException.getTransactionByIdFailed(id, e.getMessage());
    }
  }

  public Optional<TransactionResponseDto> getTransactionByHash(String hash) {
    log.info("Getting transaction by hash: {}", hash);
    try {
      Optional<Transaction> transactionOpt = transactionRepository.findByHash(Hash.of(hash));
      if (transactionOpt.isPresent()) {
        log.info("Successfully retrieved transaction with hash: {}", hash);
        return transactionOpt.map(transactionDtoMapper::toTransactionResponseDto);
      } else {
        log.warn("Transaction not found with hash: {}", hash);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error getting transaction by hash: {}", hash, e);
      throw TransactionApplicationException.getTransactionByHashFailed(hash, e.getMessage());
    }
  }

  public List<TransactionResponseDto> getTransactionsByAddress(String address) {
    log.info("Getting transactions by address: {}", address);
    try {
      List<Transaction> transactions = transactionRepository.findByAddress(Address.of(address));
      log.info("Successfully retrieved {} transactions for address: {}", transactions.size(), address);
      return transactions.stream()
          .map(transactionDtoMapper::toTransactionResponseDto)
          .toList();
    } catch (Exception e) {
      log.error("Error getting transactions by address: {}", address, e);
      throw TransactionApplicationException.getTransactionsByAddressFailed(address, e.getMessage());
    }
  }

  public List<TransactionResponseDto> getPendingTransactions() {
    log.info("Getting pending transactions");
    try {
      List<Transaction> pendingTransactions = transactionRepository.findPending();
      log.info("Successfully retrieved {} pending transactions", pendingTransactions.size());
      return pendingTransactions.stream()
          .map(transactionDtoMapper::toTransactionResponseDto)
          .toList();
    } catch (Exception e) {
      log.error("Error getting pending transactions", e);
      throw TransactionApplicationException.getPendingTransactionsFailed(e.getMessage());
    }
  }

  public CreateTransactionResponseDto createTransaction(CreateTransactionRequestDto request) {
    log.info("Creating transaction from {} to {} amount {}", 
        request.getFromAddress(), request.getToAddress(), request.getAmount());
    try {
      Address fromAddress = request.getFromAddress() != null ? Address.of(request.getFromAddress()) : null;
      Address toAddress = Address.of(request.getToAddress());
      Amount amount = Amount.of(request.getAmount());

      // Get current balance for validation if fromAddress is provided
      com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance currentBalance = null;
      if (fromAddress != null) {
        // Get balance using wallet service
        var balanceResponse = walletApplicationService.getWalletBalance(fromAddress.getValue());
        currentBalance = com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance.of(balanceResponse.getBalance());
      }

      // Validate transaction creation using domain service
      transactionValidationDomainService.validateTransactionCreation(fromAddress, toAddress, amount, currentBalance);

      Transaction transaction = Transaction.create(fromAddress, toAddress, amount);
      if (request.getSignature() != null) {
        transaction.setSignature(Signature.of(request.getSignature()));
      }

      Transaction savedTransaction = transactionRepository.save(transaction);
      log.info("Successfully created transaction with id: {} to: {} amount: {}", 
          savedTransaction.getId().getValue(), savedTransaction.getToAddress(), savedTransaction.getAmount());
      return transactionDtoMapper.toCreateTransactionResponseDto(savedTransaction);
    } catch (Exception e) {
      log.error("Error creating transaction from {} to {} amount {}", 
          request.getFromAddress(), request.getToAddress(), request.getAmount(), e);
      throw TransactionApplicationException.transactionCreationFailed(e.getMessage());
    }
  }

  public Optional<TransactionResponseDto> updateTransaction(Long id, CreateTransactionRequestDto request) {
    log.info("Updating transaction with id: {}", id);
    try {
      return transactionRepository.findById(Id.of(id))
          .filter(tx -> !tx.isMined())
          .map(existingTransaction -> {
            try {
              // Validate transaction update using domain service
              transactionValidationDomainService.validateTransactionUpdate(existingTransaction);
              
              if (request.getSignature() != null) {
                existingTransaction.setSignature(Signature.of(request.getSignature()));
              }
              Transaction updatedTransaction = transactionRepository.save(existingTransaction);
              log.info("Successfully updated transaction with id: {}", id);
              return transactionDtoMapper.toTransactionResponseDto(updatedTransaction);
            } catch (Exception e) {
              log.error("Error updating transaction with id: {}", id, e);
              throw TransactionApplicationException.transactionUpdateFailed(id, e.getMessage());
            }
          });
    } catch (Exception e) {
      log.error("Error finding transaction for update with id: {}", id, e);
      throw TransactionApplicationException.transactionUpdateFailed(id, e.getMessage());
    }
  }

  public boolean deleteTransaction(Long id) {
    log.info("Deleting transaction with id: {}", id);
    try {
      return transactionRepository.findById(Id.of(id))
          .filter(tx -> !tx.isMined())
          .map(transaction -> {
            try {
              // Validate transaction deletion using domain service
              transactionValidationDomainService.validateTransactionDeletion(transaction);
              
              transactionRepository.delete(transaction);
              log.info("Successfully deleted transaction with id: {}", id);
              return true;
            } catch (Exception e) {
              log.error("Error deleting transaction with id: {}", id, e);
              throw TransactionApplicationException.transactionDeletionFailed(id, e.getMessage());
            }
          })
          .orElse(false);
    } catch (Exception e) {
      log.error("Error finding transaction for deletion with id: {}", id, e);
      throw TransactionApplicationException.transactionDeletionFailed(id, e.getMessage());
    }
  }

}