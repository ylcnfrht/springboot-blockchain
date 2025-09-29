package com.ylcnfrht.blockchain.domain.wallet;

import java.math.BigDecimal;

import com.ylcnfrht.blockchain.domain.common.BaseEntity;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Id;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;
import com.ylcnfrht.blockchain.domain.blockchain.valueobjects.Balance;

public class Wallet extends BaseEntity<Id<Long>> {

  private Address address;
  private String publicKey;
  private String privateKey;
  private Balance balance;

  private Wallet() {
    super();
    this.balance = Balance.of(BigDecimal.ZERO);
  }

  public static Wallet create(Address address, String publicKey, String privateKey) {
    Wallet wallet = new Wallet();
    wallet.address = address;
    wallet.publicKey = publicKey;
    wallet.privateKey = privateKey;
    return wallet;
  }

  public static Wallet of(Id<Long> id, Address address, String publicKey, String privateKey, Balance balance, Boolean active) {
    Wallet wallet = new Wallet();
    wallet.setId(id);
    wallet.address = address;
    wallet.publicKey = publicKey;
    wallet.privateKey = privateKey;
    wallet.balance = balance != null ? balance : Balance.of(BigDecimal.ZERO);
    if (active != null) {
      wallet.setIsActive(active);
    }
    return wallet;
  }

  public Address getAddress() {
    return address;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public Balance getBalance() {
    return balance;
  }

  public void credit(Amount amount) {
    this.balance = this.balance.add(amount);
    markAsModified();
  }

  public void debit(Amount amount) {
    this.balance = this.balance.subtract(amount);
    markAsModified();
  }

  public void rotateKeys(String newPublicKey, String newPrivateKey) {
    this.publicKey = newPublicKey;
    this.privateKey = newPrivateKey;
    markAsModified();
  }

  public void setBalance(Balance newBalance) {
    this.balance = newBalance;
    markAsModified();
  }
}
