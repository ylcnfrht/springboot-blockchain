package com.ylcnfrht.blockchain.shared;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashUtils {
  public static String createHash(String data) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(data.getBytes());

      return String.format("%064x", new BigInteger(1, hash));
    } catch (Exception e) {
      throw new RuntimeException("Hash generation failed", e);
    }

  }
}
