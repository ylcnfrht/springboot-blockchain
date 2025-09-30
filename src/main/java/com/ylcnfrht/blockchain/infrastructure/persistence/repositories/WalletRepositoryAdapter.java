package com.ylcnfrht.blockchain.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.domain.wallet.WalletRepositoryPort;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.infrastructure.persistence.jpa.WalletJpaRepository;
import com.ylcnfrht.blockchain.infrastructure.persistence.mappers.WalletMapper;

@Repository
public class WalletRepositoryAdapter implements WalletRepositoryPort {

  private final WalletJpaRepository jpaRepository;
  private final WalletMapper mapper;

  public WalletRepositoryAdapter(WalletJpaRepository jpaRepository, WalletMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  // ReadRepository methods
  @Override
  public Optional<Wallet> findById(Id<Long> id) {
    return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
  }

  @Override
  public List<Wallet> findAll() {
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
  public List<Wallet> findByActiveTrue() {
    return jpaRepository.findByActiveTrue().stream().map(mapper::toDomain).toList();
  }

  // Wallet-specific read methods
  @Override
  public Optional<Wallet> findByAddress(String address) {
    return jpaRepository.findByAddress(address).map(mapper::toDomain);
  }

  @Override
  public Optional<Wallet> findByPublicKey(String publicKey) {
    return jpaRepository.findByPublicKey(publicKey).map(mapper::toDomain);
  }

  @Override
  public Boolean existsByAddress(String address) {
    return jpaRepository.existsByAddress(address);
  }

  // WriteRepository methods
  @Override
  public Wallet save(Wallet wallet) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(wallet)));
  }

  @Override
  public List<Wallet> saveAll(List<Wallet> wallets) {
    var entities = wallets.stream().map(mapper::toEntity).toList();
    var savedEntities = jpaRepository.saveAll(entities);
    return savedEntities.stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(Id<Long> id) {
    jpaRepository.deleteById(id.getValue());
  }

  @Override
  public void delete(Wallet wallet) {
    jpaRepository.delete(mapper.toEntity(wallet));
  }

  @Override
  public void deleteAll(List<Wallet> wallets) {
    var entities = wallets.stream().map(mapper::toEntity).toList();
    jpaRepository.deleteAll(entities);
  }

  @Override
  public Wallet saveAndFlush(Wallet wallet) {
    return mapper.toDomain(jpaRepository.saveAndFlush(mapper.toEntity(wallet)));
  }

  @Override
  public void flush() {
    jpaRepository.flush();
  }
}


