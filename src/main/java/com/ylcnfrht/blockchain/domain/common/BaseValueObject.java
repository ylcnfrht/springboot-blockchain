package com.ylcnfrht.blockchain.domain.common;

import java.util.Objects;

public abstract class BaseValueObject<T> {
  protected final T value;

  public BaseValueObject(T value) {
    this.value = value;
    validate();
  }

  protected abstract void validate();

  public T getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    BaseValueObject<?> other = (BaseValueObject<?>) obj;
    return Objects.equals(value, other.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
