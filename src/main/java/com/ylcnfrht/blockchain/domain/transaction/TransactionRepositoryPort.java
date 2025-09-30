package com.ylcnfrht.blockchain.domain.transaction;

import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.domain.common.repositories.ReadRepository;
import com.ylcnfrht.blockchain.domain.common.repositories.WriteRepository;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;


public interface TransactionRepositoryPort extends ReadRepository<Transaction, Id<Long>>, WriteRepository<Transaction, Id<Long>> {
  Optional<Transaction> findByHash(Hash hash);
  List<Transaction> findByAddress(Address address);
  List<Transaction> findPending();
}


