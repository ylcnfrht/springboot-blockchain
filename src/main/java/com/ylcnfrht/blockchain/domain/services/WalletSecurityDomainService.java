package com.ylcnfrht.blockchain.domain.services;

import org.springframework.stereotype.Service;

import com.ylcnfrht.blockchain.domain.common.DomainException;
import com.ylcnfrht.blockchain.domain.wallet.valueobjects.Address;

/**
 * Domain service responsible for wallet security operations.
 * Contains business logic for wallet creation validation, key management, and security rules.
 */
@Service
public class WalletSecurityDomainService {

    /**
     * Validates wallet creation parameters.
     * 
     * @param address wallet address
     * @param publicKey public key
     * @param privateKey private key
     * @throws DomainException if validation fails
     */
    public void validateWalletCreation(Address address, String publicKey, String privateKey) {
        // Validate address
        if (address == null) {
            throw new DomainException("Wallet address cannot be null");
        }

        if (!isValidAddressFormat(address)) {
            throw new DomainException("Invalid wallet address format");
        }

        // Validate public key
        if (publicKey == null || publicKey.trim().isEmpty()) {
            throw new DomainException("Public key cannot be null or empty");
        }

        if (!isValidKeyFormat(publicKey)) {
            throw new DomainException("Invalid public key format");
        }

        // Validate private key
        if (privateKey == null || privateKey.trim().isEmpty()) {
            throw new DomainException("Private key cannot be null or empty");
        }

        if (!isValidKeyFormat(privateKey)) {
            throw new DomainException("Invalid private key format");
        }

        // Validate key pair relationship (simplified)
        if (!isValidKeyPair(publicKey, privateKey)) {
            throw new DomainException("Public and private keys do not form a valid key pair");
        }
    }

    /**
     * Validates key rotation parameters.
     * 
     * @param newPrivateKey new private key
     * @param currentPublicKey current public key
     * @throws DomainException if validation fails
     */
    public void validateKeyRotation(String newPrivateKey, String currentPublicKey) {
        if (newPrivateKey == null || newPrivateKey.trim().isEmpty()) {
            throw new DomainException("New private key cannot be null or empty");
        }

        if (currentPublicKey == null || currentPublicKey.trim().isEmpty()) {
            throw new DomainException("Current public key cannot be null or empty");
        }

        if (!isValidKeyFormat(newPrivateKey)) {
            throw new DomainException("Invalid new private key format");
        }

        if (!isValidKeyFormat(currentPublicKey)) {
            throw new DomainException("Invalid current public key format");
        }

        // Check if new private key is different from current
        if (newPrivateKey.equals(currentPublicKey)) {
            throw new DomainException("New private key cannot be the same as current public key");
        }
    }

    /**
     * Validates if an address is unique and can be used for wallet creation.
     * 
     * @param address the address to validate
     * @param isAddressExists function to check if address already exists
     * @throws DomainException if address is not unique
     */
    public void validateAddressUniqueness(Address address, java.util.function.Function<Address, Boolean> isAddressExists) {
        if (address == null) {
            throw new DomainException("Address cannot be null");
        }

        if (isAddressExists.apply(address)) {
            throw new DomainException("Wallet with this address already exists");
        }
    }

    /**
     * Validates wallet security requirements.
     * 
     * @param address wallet address
     * @param publicKey public key
     * @param privateKey private key
     * @throws DomainException if security requirements are not met
     */
    public void validateWalletSecurity(Address address, String publicKey, String privateKey) {
        // Check minimum key length
        if (publicKey.length() < 32) {
            throw new DomainException("Public key must be at least 32 characters long");
        }

        if (privateKey.length() < 32) {
            throw new DomainException("Private key must be at least 32 characters long");
        }

        // Check for common weak patterns
        if (containsWeakPatterns(publicKey)) {
            throw new DomainException("Public key contains weak security patterns");
        }

        if (containsWeakPatterns(privateKey)) {
            throw new DomainException("Private key contains weak security patterns");
        }

        // Validate address security
        if (!isSecureAddress(address)) {
            throw new DomainException("Address does not meet security requirements");
        }
    }

    /**
     * Checks if an address has a valid format.
     * 
     * @param address the address to validate
     * @return true if the address format is valid
     */
    private boolean isValidAddressFormat(Address address) {
        if (address == null || address.getValue() == null) {
            return false;
        }

        String addressValue = address.getValue().trim();
        
        // Basic validation: address should not be empty and should have reasonable length
        return !addressValue.isEmpty() && 
               addressValue.length() >= 3 && 
               addressValue.length() <= 100 &&
               !addressValue.contains(" ") &&
               addressValue.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Checks if a key has a valid format.
     * 
     * @param key the key to validate
     * @return true if the key format is valid
     */
    private boolean isValidKeyFormat(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }

        String trimmedKey = key.trim();
        
        // Basic validation: key should have reasonable length and format
        return trimmedKey.length() >= 3 && 
               trimmedKey.length() <= 2048 &&
               !trimmedKey.contains(" ") &&
               trimmedKey.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Validates if public and private keys form a valid key pair.
     * This is a simplified validation - in a real implementation, this would use cryptographic verification.
     * 
     * @param publicKey the public key
     * @param privateKey the private key
     * @return true if the keys form a valid pair
     */
    private boolean isValidKeyPair(String publicKey, String privateKey) {
        // Simplified validation: keys should not be null
        return publicKey != null && privateKey != null;
    }

    /**
     * Checks if a key contains weak security patterns.
     * 
     * @param key the key to check
     * @return true if the key contains weak patterns
     */
    private boolean containsWeakPatterns(String key) {
        if (key == null) {
            return true;
        }

        String lowerKey = key.toLowerCase();
        
        // Check for common weak patterns
        return lowerKey.contains("password") ||
               lowerKey.contains("123456") ||
               lowerKey.contains("abcdef") ||
               lowerKey.contains("qwerty") ||
               lowerKey.matches(".*(.)\\1{3,}.*"); // Repeated characters
    }

    /**
     * Checks if an address meets security requirements.
     * 
     * @param address the address to check
     * @return true if the address is secure
     */
    private boolean isSecureAddress(Address address) {
        if (address == null || address.getValue() == null) {
            return false;
        }

        String addressValue = address.getValue();
        
        // Check for minimum entropy (simplified)
        return addressValue.length() >= 20 && 
               !addressValue.matches(".*(.)\\1{3,}.*"); // No repeated characters
    }

    /**
     * Generates security recommendations for a wallet.
     * 
     * @param address wallet address
     * @param publicKey public key
     * @param privateKey private key
     * @return security recommendations
     */
    public String generateSecurityRecommendations(Address address, String publicKey, String privateKey) {
        StringBuilder recommendations = new StringBuilder();
        
        if (publicKey.length() < 64) {
            recommendations.append("Consider using a longer public key for better security. ");
        }
        
        if (privateKey.length() < 64) {
            recommendations.append("Consider using a longer private key for better security. ");
        }
        
        if (containsWeakPatterns(publicKey) || containsWeakPatterns(privateKey)) {
            recommendations.append("Avoid using predictable patterns in keys. ");
        }
        
        if (address.getValue().length() < 30) {
            recommendations.append("Consider using a longer address for better security. ");
        }
        
        return recommendations.length() > 0 ? recommendations.toString() : "Wallet meets basic security requirements.";
    }
}
