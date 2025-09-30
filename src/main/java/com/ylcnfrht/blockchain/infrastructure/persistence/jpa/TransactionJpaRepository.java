package com.ylcnfrht.blockchain.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ylcnfrht.blockchain.infrastructure.persistence.entities.TransactionEntity;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
  
  Optional<TransactionEntity> findByTransactionHash(String transactionHash);

  List<TransactionEntity> findByFromAddress(String fromAddress);

  List<TransactionEntity> findByToAddress(String toAddress);

  @Query("SELECT t FROM TransactionEntity t WHERE t.fromAddress = :address OR t.toAddress = :address")
  List<TransactionEntity> findByAddress(@Param("address") String address);

  List<TransactionEntity> findByBlockIsNull();

  @Query("SELECT t FROM TransactionEntity t WHERE t.mined = true")
  List<TransactionEntity> findMinedTransactions();

  List<TransactionEntity> findByMinedTrue();
  
  @Query("SELECT t FROM TransactionEntity t WHERE t.mined = false ORDER BY t.timestamp ASC")
  List<TransactionEntity> findPendingTransactions();
  
  @Query("SELECT COUNT(t) FROM TransactionEntity t WHERE t.mined = false")
  long countPendingTransactions();
}


