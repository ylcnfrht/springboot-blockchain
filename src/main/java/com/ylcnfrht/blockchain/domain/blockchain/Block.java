package com.ylcnfrht.blockchain.domain.blockchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Nonce;
import com.ylcnfrht.blockchain.domain.common.BaseEntity;
import com.ylcnfrht.blockchain.domain.common.DomainException;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Timestamp;
import com.ylcnfrht.blockchain.domain.transaction.Transaction;

public class Block extends BaseEntity<Id<Long>> {

  private Hash hash;
  private Hash previousHash;
  private Timestamp timestamp;
  private Nonce nonce;
  private boolean mined;
  private List<Transaction> transactions;

  private Block() {
    super();
    this.timestamp = Timestamp.now();
    this.nonce = Nonce.zero();
    this.mined = false;
    this.transactions = new ArrayList<>();
  }

  public static Block create(Hash previousHash) {
    Block block = new Block();
    block.previousHash = previousHash;
    block.recalculateHash();
    return block;
  }

  public static Block of(Id<Long> id,
      Hash hash,
      Hash previousHash,
      Timestamp timestamp,
      Nonce nonce,
      boolean mined) {
    Block block = new Block();
    if (id != null) {
      block.setId(id);
    }
    block.hash = hash;
    block.previousHash = previousHash;
    block.timestamp = timestamp;
    block.nonce = nonce;
    block.mined = mined;

    return block;
  }

  public void addTransaction(Transaction tx) {
    if (mined) {
      throw new DomainException("Cannot add transaction to a mined block");
    }
    if (tx == null || !tx.isValid()) {
      throw new DomainException("Invalid transaction");
    }
    transactions.add(tx);
    recalculateHash();
    markAsModified();
  }

  public void incrementNonce() {
    if (mined) {
      throw new DomainException("Cannot change nonce of a mined block");
    }
    this.nonce = this.nonce.increment();
    recalculateHash();
    markAsModified();
  }

  public boolean meetsDifficulty(int difficulty) {
    return hash != null && hash.startsWithZeros(difficulty);
  }

  public void markAsMined(int difficulty) {
    if (!meetsDifficulty(difficulty)) {
      throw new DomainException("Block does not meet required difficulty");
    }
    this.mined = true;
    markAsModified();
  }

  public boolean isMined() {
    return mined;
  }

  public boolean isValid() {
    return hash != null
        && hash.equals(Hash.generate(canonicalData()))
        && (previousHash == null || !previousHash.equals(hash));
  }

  public Hash getHash() {
    return hash;
  }

  public Hash getPreviousHash() {
    return previousHash;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public Nonce getNonce() {
    return nonce;
  }

  public List<Transaction> getTransactions() {
    return Collections.unmodifiableList(transactions);
  }

  private void recalculateHash() {
    this.hash = Hash.generate(canonicalData());
  }

  private String canonicalData() {
    StringBuilder sb = new StringBuilder();
    sb.append(previousHash != null ? previousHash.getValue() : "");
    sb.append(timestamp.getValue());
    sb.append(nonce.getValue());

    for (Transaction tx : transactions) {
      // Use transaction data instead of ID for hash calculation
      sb.append(tx.getFromAddress() != null ? tx.getFromAddress().getValue() : "");
      sb.append(tx.getToAddress() != null ? tx.getToAddress().getValue() : "");
      sb.append(tx.getAmount() != null ? tx.getAmount().getValue().toString() : "");
      sb.append(tx.getTimestamp() != null ? tx.getTimestamp().getValue().toString() : "");
    }

    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format("Block{id=%s, hash=%s, prev=%s, nonce=%s, mined=%s, txCount=%d}",
        getId(), hash, previousHash, nonce, mined, transactions.size());
  }
}