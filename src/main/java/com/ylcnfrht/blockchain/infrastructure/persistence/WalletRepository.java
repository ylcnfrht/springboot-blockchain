package com.ylcnfrht.blockchain.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.wallet.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
  Optional<Wallet> findByAddress(String address);

  Optional<Wallet> findByPublicKey(String publicKey);

  List<Wallet> findByActiveTrue();

  Boolean existsByAddress(String address);
}