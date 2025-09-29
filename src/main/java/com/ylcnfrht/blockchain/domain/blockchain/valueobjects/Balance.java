package com.ylcnfrht.blockchain.domain.blockchain.valueobjects;

import java.math.BigDecimal;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Amount;

public class Balance extends BaseValueObject<BigDecimal> {

    private Balance(BigDecimal value) {
        super(value);
    }

    @Override
    protected void validate() {
        if (value == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
    }

    public static Balance of(BigDecimal value) {
        return new Balance(value);
    }

    public Balance add(Amount amount) {
        return new Balance(this.getValue().add(amount.getValue()));
    }

    public Balance subtract(Amount amount) {
        BigDecimal result = this.getValue().subtract(amount.getValue());
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        return new Balance(result);
    }
}
