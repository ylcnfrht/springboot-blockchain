package com.ylcnfrht.blockchain.domain.wallet.valueobjects;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

public class Address extends BaseValueObject<String> {

    private Address(String value) {
        super(value);
    }

    @Override
    protected void validate() {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty");
        }
        if (!isValidAddress(value)) {
            throw new IllegalArgumentException("Invalid address format");
        }
    }

    private boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }

    public static Address of(String value) {
        return new Address(value);
    }
}


