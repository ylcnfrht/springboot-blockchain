package com.ylcnfrht.blockchain.domain.common.valueobjects;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.ylcnfrht.blockchain.domain.common.BaseValueObject;

public class Hash extends BaseValueObject<String> {
    
    private Hash(String value) {
        super(value);
    }
    
    @Override
    protected void validate() {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Hash cannot be null or empty");
        }
        if (!isValidHash(value)) {
            throw new IllegalArgumentException("Invalid hash format");
        }
    }
    
    private boolean isValidHash(String hash) {
        return hash.matches("^[a-fA-F0-9]{64}$");
    }
    
    public static Hash of(String value) {
        return new Hash(value);
    }
    
    public static Hash generate(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return new Hash(hexString.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    public boolean startsWithZeros(int count) {
        if (count <= 0) return true;
        if (count > value.length()) return false;
        
        for (int i = 0; i < count; i++) {
            if (value.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }
}


