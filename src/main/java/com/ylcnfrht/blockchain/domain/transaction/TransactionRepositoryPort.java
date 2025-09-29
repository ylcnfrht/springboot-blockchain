package com.ylcnfrht.blockchain.domain.transaction;

import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

public interface TransactionRepositoryPort {
  Optional<Transaction> findById(Id<Long> id);
  Optional<Transaction> findByHash(Hash hash);
  List<Transaction> findByAddress(Address address);
  List<Transaction> findPending();
  List<Transaction> findAll();
  long count();
  Transaction save(Transaction tx);
  void delete(Transaction tx);
}


