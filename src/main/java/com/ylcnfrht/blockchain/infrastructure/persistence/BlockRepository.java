package com.ylcnfrht.blockchain.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.blockchain.Block;

@Repository
public interface BlockRepository extends JpaRepository<Block, Long> {
  Optional<Block> findByHash(String hash);

  @Query("SELECT b FROM Block b ORDER BY b.id DESC")
  List<Block> findAllOrderByIdDesc();

  @Query("SELECT b FROM Block b ORDER BY b.id DESC LIMIT 1")
  Optional<Block> findLatestBlock();

  List<Block> findByMinedTrue();

  List<Block> findByMinedFalse();
}
