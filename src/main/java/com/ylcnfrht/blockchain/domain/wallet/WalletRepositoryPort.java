package com.ylcnfrht.blockchain.domain.wallet;

import java.util.List;
import java.util.Optional;

public interface WalletRepositoryPort {
  Optional<Wallet> findByAddress(String address);
  Optional<Wallet> findByPublicKey(String publicKey);
  List<Wallet> findByActiveTrue();
  Boolean existsByAddress(String address);
  Optional<Wallet> findById(Long id);
  Wallet save(Wallet wallet);
  long count();
}