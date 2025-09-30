package com.ylcnfrht.blockchain.infrastructure.crypto;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for generating ECDSA key pairs for blockchain wallets.
 */
@Component
@Slf4j
public class KeyPairGenerator {

    private static final String ALGORITHM = "EC";
    private static final int KEY_SIZE = 256; // 256-bit ECDSA key

    /**
     * Generates a new ECDSA key pair for wallet creation.
     * 
     * @return KeyPair containing public and private keys
     * @throws RuntimeException if key generation fails
     */
    public KeyPair generateKeyPair() {
        try {
            log.debug("Generating new ECDSA key pair");
            
            java.security.KeyPairGenerator keyPairGenerator = java.security.KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEY_SIZE);
            
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            
            log.debug("Successfully generated ECDSA key pair");
            return keyPair;
            
        } catch (NoSuchAlgorithmException e) {
            log.error("ECDSA algorithm not available", e);
            throw new RuntimeException("ECDSA algorithm not supported", e);
        } catch (Exception e) {
            log.error("Error generating key pair", e);
            throw new RuntimeException("Failed to generate key pair", e);
        }
    }

    /**
     * Generates a new key pair and returns the keys as Base64 encoded strings.
     * 
     * @return KeyPairStrings containing Base64 encoded public and private keys
     */
    public KeyPairStrings generateKeyPairStrings() {
        KeyPair keyPair = generateKeyPair();
        
        String publicKeyString = ECDSASigningService.publicKeyToString(keyPair.getPublic());
        String privateKeyString = ECDSASigningService.privateKeyToString(keyPair.getPrivate());
        
        return new KeyPairStrings(publicKeyString, privateKeyString);
    }

    /**
     * Data class to hold Base64 encoded key strings.
     */
    public static class KeyPairStrings {
        private final String publicKey;
        private final String privateKey;

        public KeyPairStrings(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }
}