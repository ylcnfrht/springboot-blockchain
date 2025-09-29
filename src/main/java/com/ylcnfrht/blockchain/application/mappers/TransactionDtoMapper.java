package com.ylcnfrht.blockchain.application.mappers;

import org.springframework.stereotype.Component;

import com.ylcnfrht.blockchain.application.dtos.response.CreateTransactionResponseDto;
import com.ylcnfrht.blockchain.application.dtos.response.TransactionResponseDto;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;

@Component
public class TransactionDtoMapper {

  public CreateTransactionResponseDto toCreateTransactionResponseDto(Transaction transaction) {
    return CreateTransactionResponseDto.builder()
        .id(transaction.getId().getValue())
        .fromAddress(transaction.getFromAddress() != null ? transaction.getFromAddress().getValue() : null)
        .toAddress(transaction.getToAddress().getValue())
        .amount(transaction.getAmount().getValue())
        .signature(transaction.getSignature() != null ? transaction.getSignature().getValue() : null)
        .timestamp(transaction.getTimestamp().getValue())
        .mined(transaction.isMined())
        .build();
  }

  public TransactionResponseDto toTransactionResponseDto(Transaction transaction) {
    return TransactionResponseDto.builder()
        .id(transaction.getId().getValue())
        .fromAddress(transaction.getFromAddress() != null ? transaction.getFromAddress().getValue() : null)
        .toAddress(transaction.getToAddress().getValue())
        .amount(transaction.getAmount().getValue())
        .signature(transaction.getSignature() != null ? transaction.getSignature().getValue() : null)
        .timestamp(transaction.getTimestamp().getValue())
        .mined(transaction.isMined())
        .build();
  }
}
