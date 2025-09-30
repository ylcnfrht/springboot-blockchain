package com.ylcnfrht.blockchain.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ylcnfrht.blockchain.infrastructure.persistence.entities.WalletEntity;

public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
  
  // Read methods
  Optional<WalletEntity> findByAddress(String address);
  Optional<WalletEntity> findByPublicKey(String publicKey);
  List<WalletEntity> findByActiveTrue();
  Boolean existsByAddress(String address);
  
  // Custom queries
  @Query("SELECT w FROM WalletEntity w WHERE w.active = true ORDER BY w.createdAt DESC")
  List<WalletEntity> findActiveWalletsOrderByCreatedAt();
  
  @Query("SELECT COUNT(w) FROM WalletEntity w WHERE w.active = true")
  long countActiveWallets();
}


