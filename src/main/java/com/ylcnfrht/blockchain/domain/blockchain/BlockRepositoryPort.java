package com.ylcnfrht.blockchain.domain.blockchain;

import java.util.List;
import java.util.Optional;

import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;

public interface BlockRepositoryPort {
  Optional<Block> findById(Id<Long> id);
  Optional<Block> findByHash(Hash hash);
  Optional<Block> findLatest();
  List<Block> findAllOrderByIdDesc();
  List<Block> findByMined(boolean mined);
  long count();
  Block save(Block block);
}

