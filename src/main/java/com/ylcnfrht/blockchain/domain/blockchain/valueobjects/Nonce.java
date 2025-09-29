package com.ylcnfrht.blockchain.domain.blockchain.valueobjects;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

public class Nonce extends BaseValueObject<Long> {

  private Nonce(Long value) {
    super(value);
  }

  public static Nonce of(Long value) {
    return new Nonce(value);
  }

  public static Nonce zero() {
    return new Nonce(0L);
  }

  public Nonce increment() {
    return new Nonce(this.value + 1);
  }

  @Override
  protected void validate() {
    if (value == null) {
      throw new IllegalArgumentException("Nonce cannot be null");
    }
    if (value < 0) {
      throw new IllegalArgumentException("Nonce cannot be negative");
    }
  }
}