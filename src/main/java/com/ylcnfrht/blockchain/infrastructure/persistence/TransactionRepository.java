package com.ylcnfrht.blockchain.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.blockchain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  Optional<Transaction> findByTransactionHash(String transactionHash);

  List<Transaction> findByFromAddress(String fromAddress);

  List<Transaction> findByToAddress(String toAddress);

  @Query("SELECT t FROM Transaction t WHERE t.fromAddress = :address OR t.toAddress = :address")
  List<Transaction> findByAddress(@Param("address") String address);

  List<Transaction> findByBlockIsNull();

  @Query("SELECT t FROM Transaction t WHERE t.mined = true")
  List<Transaction> findMinedTransactions();

  List<Transaction> findByMinedTrue();

}
