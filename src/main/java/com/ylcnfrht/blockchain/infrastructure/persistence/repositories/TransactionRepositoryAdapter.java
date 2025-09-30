package com.ylcnfrht.blockchain.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;
import com.ylcnfrht.blockchain.domain.transaction.TransactionRepositoryPort;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;
import com.ylcnfrht.blockchain.infrastructure.persistence.jpa.TransactionJpaRepository;
import com.ylcnfrht.blockchain.infrastructure.persistence.mappers.TransactionMapper;

@Repository
public class TransactionRepositoryAdapter implements TransactionRepositoryPort {

  private final TransactionJpaRepository jpaRepository;
  private final TransactionMapper mapper;

  public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository, TransactionMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Optional<Transaction> findById(Id<Long> id) {
    return jpaRepository.findById(id.getValue()).map(mapper::toDomain);
  }

  @Override
  public List<Transaction> findAll() {
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
  public Optional<Transaction> findByHash(Hash hash) {
    return jpaRepository.findByTransactionHash(hash.getValue()).map(mapper::toDomain);
  }

  @Override
  public List<Transaction> findByAddress(Address address) {
    return jpaRepository.findByAddress(address.getValue()).stream().map(mapper::toDomain).toList();
  }

  @Override
  public List<Transaction> findPending() {
    return jpaRepository.findByBlockIsNull().stream().map(mapper::toDomain).toList();
  }

  @Override
  public Transaction save(Transaction transaction) {
    return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
  }

  @Override
  public List<Transaction> saveAll(List<Transaction> transactions) {
    var entities = transactions.stream().map(mapper::toEntity).toList();
    var savedEntities = jpaRepository.saveAll(entities);
    return savedEntities.stream().map(mapper::toDomain).toList();
  }

  @Override
  public void deleteById(Id<Long> id) {
    jpaRepository.deleteById(id.getValue());
  }

  @Override
  public void delete(Transaction transaction) {
    jpaRepository.delete(mapper.toEntity(transaction));
  }

  @Override
  public void deleteAll(List<Transaction> transactions) {
    var entities = transactions.stream().map(mapper::toEntity).toList();
    jpaRepository.deleteAll(entities);
  }

  @Override
  public Transaction saveAndFlush(Transaction transaction) {
    return mapper.toDomain(jpaRepository.saveAndFlush(mapper.toEntity(transaction)));
  }

  @Override
  public void flush() {
    jpaRepository.flush();
  }
}


