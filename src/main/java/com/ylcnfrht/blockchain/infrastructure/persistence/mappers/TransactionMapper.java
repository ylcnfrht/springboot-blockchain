package com.ylcnfrht.blockchain.infrastructure.persistence.mappers;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Timestamp;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.infrastructure.persistence.entities.TransactionEntity;
import com.ylcnfrht.blockchain.infrastructure.persistence.entities.BlockEntity;


@Component
public class TransactionMapper {
  public Transaction toDomain(TransactionEntity entity) {
    if (entity == null) return null;
    return Transaction.of(
        Id.of(entity.getId()),
        entity.getFromAddress() != null ? Address.of(entity.getFromAddress()) : null,
        entity.getToAddress() != null ? Address.of(entity.getToAddress()) : null,
        Amount.of(entity.getAmount()),
        entity.getSignature() != null ? Signature.of(entity.getSignature()) : null,
        entity.getTimestamp() != null ? Timestamp.of(entity.getTimestamp()) : Timestamp.now(),
        entity.isMined());
  }

  public TransactionEntity toEntity(Transaction transaction) {
    if (transaction == null) return null;
    TransactionEntity entity = new TransactionEntity();
    entity.setId(transaction.getId() != null ? transaction.getId().getValue() : null);
    entity.setAmount(transaction.getAmount() != null ? transaction.getAmount().getValue() : BigDecimal.ZERO);
    entity.setFromAddress(transaction.getFromAddress() != null ? transaction.getFromAddress().getValue() : null);
    entity.setToAddress(transaction.getToAddress() != null ? transaction.getToAddress().getValue() : null);
    entity.setSignature(transaction.getSignature() != null ? transaction.getSignature().getValue() : null);
    entity.setTimestamp(transaction.getTimestamp() != null ? transaction.getTimestamp().getValue() : null);
    entity.setMined(transaction.isMined());
    // block will be set by repository when needed (saveWithBlock)
    return entity;
  }
}
