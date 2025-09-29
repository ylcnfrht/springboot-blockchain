package com.ylcnfrht.blockchain.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.wallet.Wallet;
import com.ylcnfrht.blockchain.domain.wallet.WalletRepositoryPort;
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

  @Override
  public Optional<Wallet> findByAddress(String address) {
    return jpaRepository.findByAddress(address).map(mapper::toDomain);
  }

  @Override
  public Optional<Wallet> findByPublicKey(String publicKey) {
    return jpaRepository.findByPublicKey(publicKey).map(mapper::toDomain);
  }

  @Override
  public List<Wallet> findByActiveTrue() {
    return jpaRepository.findByActiveTrue().stream().map(mapper::toDomain).toList();
  }

  @Override
  public Boolean existsByAddress(String address) {
    return jpaRepository.existsByAddress(address);
  }

  @Override
  public Optional<Wallet> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Wallet save(Wallet wallet) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(wallet)));
  }

  @Override
  public long count() {
    return jpaRepository.count();
  }
}


