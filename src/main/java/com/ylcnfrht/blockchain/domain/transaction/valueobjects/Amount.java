package com.ylcnfrht.blockchain.domain.transaction.valueobjects;

import java.math.BigDecimal;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

public class Amount extends BaseValueObject<BigDecimal> {
  public Amount(BigDecimal value) {
    super(value);
  }

  @Override
  protected void validate() {
    if (value == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }

    if (value.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Amount cannot be negative");
    }
  }

  public boolean isZero() {
    return value.compareTo(BigDecimal.ZERO) == 0;
  }

  public static Amount of(BigDecimal value) {
    return new Amount(value);
  }

  public static Amount of(String value) {
    return new Amount(new BigDecimal(value));
  }

  public static Amount of(double value) {
    return new Amount(BigDecimal.valueOf(value));
  }

  public BigDecimal add(Amount amount) {
    return value.add(amount.getValue());
  }

  public BigDecimal subtract(Amount amount) {
    amount.validate();
    return value.subtract(amount.getValue());
  }

  public BigDecimal multiply(Amount amount) {
    amount.validate();
    return value.multiply(amount.getValue());
  }

  public BigDecimal divide(Amount amount) {
    amount.validate();
    return value.divide(amount.getValue());
  }

  @Override
  public String toString() {
    return String.format("Amount{%s}", value);
  }
}


