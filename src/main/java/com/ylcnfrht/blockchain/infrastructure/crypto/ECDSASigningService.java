package com.ylcnfrht.blockchain.infrastructure.crypto;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.ylcnfrht.blockchain.domain.common.services.SigningService;
import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;

import lombok.extern.slf4j.Slf4j;

/**
 * ECDSA-based implementation of SigningService for cryptographic transaction signing.
 * Uses SHA256withECDSA algorithm for signing and verification.
 */
@Service
@Slf4j
public class ECDSASigningService implements SigningService {

    private static final String ALGORITHM = "EC";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    @Override
    public com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature sign(PrivateKey privateKey, Hash dataHash) {
        try {
            log.debug("Signing data hash: {}", dataHash.getValue());
            
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(dataHash.getValue().getBytes());
            
            byte[] signatureBytes = signature.sign();
            String signatureString = Base64.getEncoder().encodeToString(signatureBytes);
            
            log.debug("Generated signature: {}", signatureString);
            return com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature.of(signatureString);
            
        } catch (Exception e) {
            log.error("Error signing data hash: {}", dataHash.getValue(), e);
            throw new RuntimeException("Failed to sign transaction", e);
        }
    }

    @Override
    public boolean verify(PublicKey publicKey, Hash dataHash, com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature signature) {
        try {
            log.debug("Verifying signature for data hash: {}", dataHash.getValue());
            
            Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
            sig.initVerify(publicKey);
            sig.update(dataHash.getValue().getBytes());
            
            byte[] signatureBytes = Base64.getDecoder().decode(signature.getValue());
            boolean isValid = sig.verify(signatureBytes);
            
            log.debug("Signature verification result: {}", isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("Error verifying signature for data hash: {}", dataHash.getValue(), e);
            return false;
        }
    }

    @Override
    public String algorithm() {
        return "ECDSA_SHA256";
    }

    /**
     * Creates a PublicKey from a Base64 encoded string
     */
    public static PublicKey createPublicKey(String publicKeyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create public key from string", e);
        }
    }

    /**
     * Creates a PrivateKey from a Base64 encoded string
     */
    public static PrivateKey createPrivateKey(String privateKeyString) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create private key from string", e);
        }
    }

    /**
     * Converts a PublicKey to Base64 encoded string
     */
    public static String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Converts a PrivateKey to Base64 encoded string
     */
    public static String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }
}