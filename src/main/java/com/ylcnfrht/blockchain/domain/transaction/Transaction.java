package com.ylcnfrht.blockchain.domain.transaction;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.ylcnfrht.blockchain.domain.common.BaseEntity;
import com.ylcnfrht.blockchain.domain.common.DomainException;
import com.ylcnfrht.blockchain.domain.common.services.SigningService;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Timestamp;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

public class Transaction extends BaseEntity<Id<Long>> {

  private Address fromAddress;
  private Address toAddress;
  private Amount amount;
  private Signature signature;
  private Timestamp timestamp;
  private boolean mined;

  private Transaction() {
    super();
    this.mined = false;
    this.timestamp = Timestamp.now();
  }

  private Transaction(Id<Long> id) {
    super(id);
    this.mined = false;
    this.timestamp = Timestamp.now();
  }

  public static Transaction create(Address fromAddress, Address toAddress, Amount amount) {
    Transaction transaction = new Transaction();
    transaction.fromAddress = fromAddress;
    transaction.toAddress = toAddress;
    transaction.amount = amount;
    return transaction;
  }

  public static Transaction of(
      Id<Long> id, Address fromAddress, Address toAddress,
      Amount amount, Signature signature, Timestamp timestamp, boolean mined) {
    Transaction transaction = new Transaction(id);
    transaction.fromAddress = fromAddress;
    transaction.toAddress = toAddress;
    transaction.amount = amount;
    transaction.signature = signature;
    transaction.timestamp = timestamp;
    transaction.mined = mined;
    return transaction;
  }

  public void sign(PrivateKey privateKey, SigningService signingService) {
    if (privateKey == null) {
      throw new DomainException("Private key cannot be null");
    }

    if (isSigned()) {
      throw new DomainException("Transaction is already signed");
    }

    if (isMined()) {
      throw new DomainException("Cannot sign mined transaction");
    }

    Hash hash = Hash.generate(this.calculateData());
    this.signature = signingService.sign(privateKey, hash);
    markAsModified();
  }

  public boolean isSigned() {
    return signature != null;
  }

  public boolean verifySignature(PublicKey publicKey, SigningService signature) {
    if (!isSigned()) {
      return false;
    }
    if (publicKey == null) {
      throw new DomainException("Public key cannot be null");
    }

    Hash hash = Hash.generate(this.calculateData());
    return signature.verify(publicKey, hash, this.signature);
  }

  public boolean isValid() {
    return fromAddress != null &&
        toAddress != null &&
        amount != null &&
        !amount.isZero() &&
        !fromAddress.equals(toAddress);
  }

  public boolean isMined() {
    return mined;
  }

  public void markAsMined() {
    if (!isMined()) {
      this.mined = true;
      markAsModified();
    }
  }

  public boolean canBeMined() {
    return isValid() && isSigned() && !isMined();
  }

  public Address getFromAddress() {
    return fromAddress;
  }

  public Address getToAddress() {
    return toAddress;
  }

  public Amount getAmount() {
    return amount;
  }

  public Signature getSignature() {
    return signature;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setFromAddress(Address fromAddress) {
    this.fromAddress = fromAddress;
    markAsModified();
  }

  public void setToAddress(Address toAddress) {
    this.toAddress = toAddress;
    markAsModified();
  }

  public void setAmount(Amount amount) {
    this.amount = amount;
    markAsModified();
  }

  public void setSignature(Signature signature) {
    this.signature = signature;
    markAsModified();
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
    markAsModified();
  }

  public void setMined(boolean mined) {
    this.mined = mined;
    markAsModified();
  }

  private String calculateData() {
    StringBuilder data = new StringBuilder();
    data.append(fromAddress != null ? fromAddress.getValue() : "");
    data.append(toAddress.getValue());
    data.append(amount.getValue().toString());
    data.append(timestamp.getValue().toString());
    return data.toString();
  }

  @Override
  public String toString() {
    return String.format(
        "Transaction{id=%s, fromAddress=%s, toAddress=%s, amount=%s, signed=%s, mined=%s, active=%s, createdAt=%s, updatedAt=%s}",
        getId(), fromAddress, toAddress, amount, isSigned(), mined, isActive(), getCreatedAt(), getUpdatedAt());
  }

}


