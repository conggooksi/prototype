package com.secondwind.prototype;

import static org.junit.jupiter.api.Assertions.*;

import java.security.SecureRandom;
import java.util.Base64;
import org.junit.jupiter.api.Test;

class PrototypeApplicationTest {

  @Test
  void createSecretKey() {
    int keyLength = 128; // 256 bits
    byte[] keyBytes = new byte[keyLength];
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.nextBytes(keyBytes);
    String secretKey = Base64.getEncoder().encodeToString(keyBytes);

    System.out.println("Generated Secret Key: " + secretKey);
  }

}