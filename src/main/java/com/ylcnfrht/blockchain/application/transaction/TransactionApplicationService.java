package com.ylcnfrht.blockchain.application.transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ylcnfrht.blockchain.application.wallet.WalletApplicationService;
import com.ylcnfrht.blockchain.domain.blockchain.Transaction;
import com.ylcnfrht.blockchain.infrastructure.persistence.TransactionRepository;
import com.ylcnfrht.blockchain.infrastructure.web.dto.request.CreateTransactionRequest;
import com.ylcnfrht.blockchain.shared.HashUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionApplicationService {

  private final TransactionRepository transactionRepository;
  private final WalletApplicationService walletApplicationService;

  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
  }

  public Optional<Transaction> getTransactionById(Long id) {
    return transactionRepository.findById(id);
  }

  public Optional<Transaction> getTransactionByHash(String hash) {
    return transactionRepository.findByTransactionHash(hash);
  }

  public List<Transaction> getTransactionsByAddress(String address) {
    return transactionRepository.findByAddress(address);
  }

  public List<Transaction> getPendingTransactions() {
    return transactionRepository.findByBlockIsNull();
  }

  public Transaction createTransaction(CreateTransactionRequest request) {
    if (request.getFromAddress() != null &&
        !walletApplicationService.hasEnoughBalance(request.getFromAddress(), request.getAmount())) {
      throw new IllegalArgumentException("Insufficient balance");
    }

    String transactionHash = HashUtils.createHash(request.getFromAddress() +
        request.getToAddress() +
        request.getAmount());

    Transaction transaction = Transaction.builder()
        .fromAddress(request.getFromAddress())
        .toAddress(request.getToAddress())
        .amount(request.getAmount())
        .signature(request.getSignature())
        .timestamp(LocalDateTime.now())
        .transactionHash(transactionHash)
        .build();

    Transaction savedTransaction = transactionRepository.save(transaction);
    log.info("Created new transaction with hash: {}", savedTransaction.getTransactionHash());
    return savedTransaction;
  }

  public Optional<Transaction> updateTransaction(Long id, CreateTransactionRequest request) {
    return transactionRepository.findById(id)
        .filter(tx -> tx.getBlock() == null)
        .map(existingTransaction -> {
          existingTransaction.setSignature(request.getSignature());

          Transaction updatedTransaction = transactionRepository.save(existingTransaction);
          log.info("Updated transaction with id: {}", id);
          return updatedTransaction;
        });
  }

  public boolean deleteTransaction(Long id) {
    return transactionRepository.findById(id)
        .filter(tx -> tx.getBlock() == null)
        .map(transaction -> {
          transactionRepository.delete(transaction);
          log.info("Deleted transaction with id: {}", id);
          return true;
        })
        .orElse(false);
  }
}