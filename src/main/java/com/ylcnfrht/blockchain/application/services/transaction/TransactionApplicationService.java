package com.ylcnfrht.blockchain.application.services.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.dtos.request.CreateTransactionRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;
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
    return transactionRepository.findAll().stream()
        .map(transactionDtoMapper::toTransactionResponseDto)
        .toList();
  }

  public Optional<TransactionResponseDto> getTransactionById(Long id) {
    return transactionRepository.findById(Id.of(id))
        .map(transactionDtoMapper::toTransactionResponseDto);
  }

  public Optional<TransactionResponseDto> getTransactionByHash(String hash) {
    return transactionRepository.findByHash(Hash.of(hash))
        .map(transactionDtoMapper::toTransactionResponseDto);
  }

  public List<TransactionResponseDto> getTransactionsByAddress(String address) {
    return transactionRepository.findByAddress(Address.of(address)).stream()
        .map(transactionDtoMapper::toTransactionResponseDto)
        .toList();
  }

  public List<TransactionResponseDto> getPendingTransactions() {
    return transactionRepository.findPending().stream()
        .map(transactionDtoMapper::toTransactionResponseDto)
        .toList();
  }

  public CreateTransactionResponseDto createTransaction(CreateTransactionRequestDto request) {
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
    log.info("Created new transaction to: {} amount: {}", savedTransaction.getToAddress(), savedTransaction.getAmount());
    return transactionDtoMapper.toCreateTransactionResponseDto(savedTransaction);
  }

  public Optional<TransactionResponseDto> updateTransaction(Long id, CreateTransactionRequestDto request) {
    return transactionRepository.findById(Id.of(id))
        .filter(tx -> !tx.isMined())
        .map(existingTransaction -> {
          // Validate transaction update using domain service
          transactionValidationDomainService.validateTransactionUpdate(existingTransaction);
          
          if (request.getSignature() != null) {
            existingTransaction.setSignature(Signature.of(request.getSignature()));
          }
            Transaction updatedTransaction = transactionRepository.save(existingTransaction);
            log.info("Updated transaction with id: {}", id);
            return transactionDtoMapper.toTransactionResponseDto(updatedTransaction);
        });
  }

  public boolean deleteTransaction(Long id) {
    return transactionRepository.findById(Id.of(id))
        .filter(tx -> !tx.isMined())
        .map(transaction -> {
          // Validate transaction deletion using domain service
          transactionValidationDomainService.validateTransactionDeletion(transaction);
          
          transactionRepository.delete(transaction);
          log.info("Deleted transaction with id: {}", id);
          return true;
        })
        .orElse(false);
  }

}