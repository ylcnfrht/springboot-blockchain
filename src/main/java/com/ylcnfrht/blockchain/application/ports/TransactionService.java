package com.ylcnfrht.blockchain.application.ports;

import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.application.dtos.request.CreateTransactionRequestDto;
import com.ylcnfrht.blockchain.application.dtos.request.SignTransactionRequestDto;
import com.ylcnfrht.blockchain.application.dtos.response.CreateTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.SignTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;

public interface TransactionService {
  List<TransactionResponseDto> getAllTransactions();
  Optional<TransactionResponseDto> getTransactionById(Long id);
  Optional<TransactionResponseDto> getTransactionByHash(String hash);
  List<TransactionResponseDto> getTransactionsByAddress(String address);
  List<TransactionResponseDto> getPendingTransactions();
  CreateTransactionResponseDto createTransaction(CreateTransactionRequestDto request);
  Optional<TransactionResponseDto> updateTransaction(Long id, CreateTransactionRequestDto request);
  boolean deleteTransaction(Long id);
  SignTransactionResponseDto signTransaction(SignTransactionRequestDto request);
}
