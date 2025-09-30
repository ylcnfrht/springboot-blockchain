package com.ylcnfrht.blockchain.domain.wallet;

import java.util.Optional;

import com.ylcnfrht.blockchain.domain.common.repositories.ReadRepository;
import com.ylcnfrht.blockchain.domain.common.repositories.WriteRepository;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;

public interface WalletRepositoryPort extends ReadRepository<Wallet, Id<Long>>, WriteRepository<Wallet, Id<Long>> {
  
  // Wallet-specific read methods
  Optional<Wallet> findByAddress(String address);
  Optional<Wallet> findByPublicKey(String publicKey);
  Boolean existsByAddress(String address);
}