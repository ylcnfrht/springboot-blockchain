package com.ylcnfrht.blockchain.domain.blockchain;

import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.domain.common.repositories.ReadRepository;
import com.ylcnfrht.blockchain.domain.common.repositories.WriteRepository;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;


public interface BlockRepositoryPort extends ReadRepository<Block, Id<Long>>, WriteRepository<Block, Id<Long>> {
  
  // Block-specific read methods
  Optional<Block> findByHash(Hash hash);
  Optional<Block> findLatest();
  List<Block> findAllOrderByIdDesc();
  List<Block> findByMined(boolean mined);
}

