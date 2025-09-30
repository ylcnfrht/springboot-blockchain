package com.ylcnfrht.blockchain.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.blockchain.Block;
import com.ylcnfrht.blockchain.domain.blockchain.BlockRepositoryPort;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.infrastructure.persistence.jpa.BlockJpaRepository;
import com.ylcnfrht.blockchain.infrastructure.persistence.mappers.BlockMapper;

@Repository
public class BlockRepositoryAdapter implements BlockRepositoryPort {

  private final BlockJpaRepository jpaRepository;
  private final BlockMapper mapper;

  public BlockRepositoryAdapter(BlockJpaRepository jpaRepository, BlockMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Block> findById(Id<Long> id) {
    return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
  }

  @Override
  public List<Block> findAll() {
    return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsById(Id<Long> id) {
    return jpaRepository.existsById(id.getValue());
  }

  @Override
  public long count() {
    return jpaRepository.count();
  }


  @Override
  public Optional<Block> findByHash(Hash hash) {
    return jpaRepository.findByHash(hash.getValue()).map(mapper::toDomain);
  }

  @Override
  public Optional<Block> findLatest() {
    return jpaRepository.findLatestBlock().map(mapper::toDomain);
  }

  @Override
  public List<Block> findAllOrderByIdDesc() {
    return jpaRepository.findAllOrderByIdDesc().stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Block> findByMined(boolean mined) {
    return (mined ? jpaRepository.findByMinedTrue() : jpaRepository.findByMinedFalse()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public Block save(Block block) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(block)));
  }

  @Override
  public List<Block> saveAll(List<Block> blocks) {
    var entities = blocks.stream().map(mapper::toEntity).toList();
    var savedEntities = jpaRepository.saveAll(entities);
    return savedEntities.stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(Id<Long> id) {
    jpaRepository.deleteById(id.getValue());
  }

  @Override
  public void delete(Block block) {
    jpaRepository.delete(mapper.toEntity(block));
  }

  @Override
  public void deleteAll(List<Block> blocks) {
    var entities = blocks.stream().map(mapper::toEntity).toList();
    jpaRepository.deleteAll(entities);
  }

  @Override
  public Block saveAndFlush(Block block) {
    return mapper.toDomain(jpaRepository.saveAndFlush(mapper.toEntity(block)));
  }

  @Override
  public void flush() {
    jpaRepository.flush();
  }
}


