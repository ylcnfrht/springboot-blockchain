package com.ylcnfrht.blockchain.infrastructure.persistence.mappers;

import org.springframework.stereotype.Component;

import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Nonce;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Timestamp;
import com.ylcnfrht.blockchain.infrastructure.persistence.entities.BlockEntity;

@Component
public class BlockMapper {

  public Block toDomain(BlockEntity entity) {
    if (entity == null)
      return null;
    Block block = Block.of(
        entity.getId() != null ? Id.of(entity.getId()) : null,
        entity.getHash() != null && !entity.getHash().trim().isEmpty() ? Hash.of(entity.getHash()) : null,
        entity.getPreviousHash() != null && !entity.getPreviousHash().trim().isEmpty()
            ? Hash.of(entity.getPreviousHash())
            : null,
        entity.getTimestamp() != null ? Timestamp.of(entity.getTimestamp()) : null,
        Nonce.of(entity.getNonce() != null ? entity.getNonce().longValue() : 0L),
        Boolean.TRUE.equals(entity.getMined()));

    return block;
  }

  public BlockEntity toEntity(Block domain) {
    if (domain == null)
      return null;
    BlockEntity entity = new BlockEntity();
    entity.setId(domain.getId() != null ? domain.getId().getValue() : null);
    entity.setHash(domain.getHash() != null ? domain.getHash().getValue() : null);
    entity.setPreviousHash(domain.getPreviousHash() != null ? domain.getPreviousHash().getValue() : null);
    entity.setTimestamp(domain.getTimestamp() != null ? domain.getTimestamp().getValue() : null);
    entity.setNonce(domain.getNonce() != null ? domain.getNonce().getValue().intValue() : 0);
    entity.setMined(domain.isMined());
    return entity;
  }
}
