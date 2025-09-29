package com.ylcnfrht.blockchain.domain.common.services;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.ylcnfrht.blockchain.domain.common.valueobjects.Hash;
import com.ylcnfrht.blockchain.domain.transaction.valueobjects.Signature;

/**
 * Cryptography service abstraction for signing and verifying domain data.
 * Domain-level interface; implementations live in infrastructure.
 */
public interface SigningService {

  /**
   * Signs the given data hash with the provided private key.
   * @param privateKey domain private key value object
   * @param dataHash hash of the data to be signed (e.g., SHA-256)
   * @return immutable Signature value object
   */
  Signature sign(PrivateKey privateKey, Hash dataHash);

  /**
   * Verifies a signature against the given data hash and public key.
   * @param publicKey domain public key value object
   * @param dataHash hash of the signed data
   * @param signature signature to verify
   * @return true if signature is valid for the given key and data hash
   */
  boolean verify(PublicKey publicKey, Hash dataHash, Signature signature);

  /**
   * Returns the algorithm identifier (e.g., "ECDSA_SHA256").
   */
  default String algorithm() {
    return "UNSPECIFIED";
  }
}


