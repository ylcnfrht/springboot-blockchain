package com.ylcnfrht.blockchain.domain.common.valueobjects;

import java.util.UUID;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

public class Id<T> extends BaseValueObject<T> {
    private Id(T value) {
        super(value);
        validate();
    }

    @Override
    protected void validate() {
        if (value == null) {
            throw new IllegalArgumentException("ID value cannot be null");
        }
    }
    
    public static <T> Id<T> of(T value) {
        return new Id<>(value);
    }
    
    public T getValue() {
        return value;
    }
    
    public static Id<String> generate() {
        return new Id<>(UUID.randomUUID().toString());
    }}
