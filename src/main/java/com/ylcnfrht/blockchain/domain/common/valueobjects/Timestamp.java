package com.ylcnfrht.blockchain.domain.common.valueobjects;

import java.time.LocalDateTime;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

/**
 * Timestamp value object for domain entities.
 */
public class Timestamp extends BaseValueObject<LocalDateTime> {
    
    private Timestamp(LocalDateTime value) {
        super(value);
    }
    
    @Override
    protected void validate() {
        if (value == null) {
            throw new IllegalArgumentException("Timestamp value cannot be null");
        }
    }
    
    public static Timestamp of(LocalDateTime value) {
        return new Timestamp(value);
    }
    
    public static Timestamp now() {
        return new Timestamp(LocalDateTime.now());
    }
}