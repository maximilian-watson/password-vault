package com.mwatson.passwordvault.crypto;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Service for encrypting and decrypting sensitive data using AES-GCM.
 * Provides authenticated encryption with password-based key derivation.
 */
public class EncryptionService {

  private static final String ALGORITHM = "AES/GCM/NoPadding";
  private static final String KEY_DERIVATION_ALGORITHM = "PBKDF2WithHmacSHA256";
  private static final int KEY_LENGTH = 256; // AES-256
  private static final int GCM_TAG_LENGTH = 128; // bits
  private static final int GCM_IV_LENGTH = 12; // bytes
  private static final int ITERATION_COUNT = 100_000; // PBKDF2 iterations

  private final SecureRandom secureRandom;

  public EncryptionService() {
    this.secureRandom = new SecureRandom();
  }

  /**
   * Encrypts data using AES-GCM with a password-derived key.
   *
   * @param data The plaintext to encrypt
   * @param password The password for key derivation
   * @param salt The salt for key derivation, stored with encrypted data
   * @return encrypted data concatentated with IV: IV + encrypted data
   */
  public byte[] encrypt(String data, char[] password, byte[] salt) {
    if (data == null || password == null || salt == null) {
      throw new IllegalArgumentException("Data, password, salt can't be null");
    }

    try {
      SecretKey secretKey = deriveKey(password, salt);
      byte[] iv = new byte[GCM_IV_LENGTH];
      secureRandom.nextBytes(iv);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);

      byte[] encryptedData = cipher.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
      return ByteBuffer.allocate(iv.length + encryptedData.length).put(iv).put(encryptedData)
          .array();
    } catch (Exception e) {
      throw new EncryptionException("Failed to encrypt data", e);
    }
  }

  public String decrypt(byte[] encryptedData, char[] password, byte[] salt) {

  }

  private SecretKey deriveKey(char[] password, byte[] salt) {

  }

  public static class EncryptionException extends RuntimeException {
    public EncryptionException(String message, Throwable cause) {
      super(message, cause);
    }

    public EncryptionException(String message) {
      super(message);
    }
  }

}
