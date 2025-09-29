package com.ylcnfrht.blockchain.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ylcnfrht.blockchain.infrastructure.persistence.entities.BlockEntity;

public interface BlockJpaRepository extends JpaRepository<BlockEntity, Long> {
  Optional<BlockEntity> findByHash(String hash);

  @Query("SELECT b FROM BlockEntity b ORDER BY b.id DESC")
  List<BlockEntity> findAllOrderByIdDesc();

  @Query(value = "SELECT * FROM blocks ORDER BY id DESC LIMIT 1", nativeQuery = true)
  Optional<BlockEntity> findLatestBlock();

  List<BlockEntity> findByMinedTrue();

  List<BlockEntity> findByMinedFalse();
}


