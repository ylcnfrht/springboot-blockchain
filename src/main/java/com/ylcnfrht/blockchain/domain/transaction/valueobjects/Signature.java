package com.ylcnfrht.blockchain.domain.transaction.valueobjects;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

public class Signature extends BaseValueObject<String> {

  private Signature(String value) {
    super(value);
  }

  public static Signature of(String value) {
    return new Signature(value);
  }

  @Override
  protected void validate() {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Signature cannot be null or empty");
    }
  }
}


