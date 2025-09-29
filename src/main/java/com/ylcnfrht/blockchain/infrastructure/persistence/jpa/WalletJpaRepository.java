package com.ylcnfrht.blockchain.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ylcnfrht.blockchain.infrastructure.persistence.entities.WalletEntity;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
  Optional<WalletEntity> findByAddress(String address);
  Optional<WalletEntity> findByPublicKey(String publicKey);
  List<WalletEntity> findByActiveTrue();
  Boolean existsByAddress(String address);
}


