package com.mwatson.passwordvault.crypto;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

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

  public String decrypt(byte[] encryptedDataWithIv, char[] password, byte[] salt) {
    if(encryptedDataWithIv == null || password == null || salt == null) {
      throw new IllegalArgumentException("Encrypted Data, password, salt can't be null");
    }
    
    if(encryptedDataWithIv.length < GCM_IV_LENGTH) {
      throw new IllegalArgumentException("Encrypted data is too short to contain IV");
    }
    
    try {
      ByteBuffer buffer = ByteBuffer.wrap(encryptedDataWithIv);
      byte[] iv = new byte[GCM_IV_LENGTH];
      buffer.get(iv);

      byte[] encryptedData = new byte[buffer.remaining()];
      buffer.get(encryptedData);

      SecretKey secretKey = deriveKey(password, salt);

      Cipher cipher = Cipher.getInstance(ALGORITHM);
      GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec);

      byte[] decryptedData = cipher.doFinal(encryptedData);

      return new String(decryptedData, java.nio.charset.StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new EncryptionException("Failed to decrypt data", e);
    }
  }

  private SecretKey deriveKey(char[] password, byte[] salt) {
    try {
      SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_DERIVATION_ALGORITHM);

      KeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH);
      SecretKey tmpKey = factory.generateSecret(spec);
      return new SecretKeySpec(tmpKey.getEncoded(), "AES");
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new EncryptionException("Failed to derive key", e);
    }
  }

  public byte[] generateSalt() {
    byte[] salt = new byte[16];
    secureRandom.nextBytes(salt);
    return salt;
  }

  public String encryptToBase64(String data, char[] password, byte[] salt) {
    byte[] encrypted = encrypt(data, password, salt);
    return Base64.getEncoder().encodeToString(encrypted);
  }

  public String decryptFromBase64(String base64Data, char[] password, byte[] salt) {
    byte[] encryptedData = Base64.getDecoder().decode(base64Data);
    return decrypt(encryptedData, password, salt);
  }

  public void clearPassword(char[] password) {
    if (password != null) {
      Arrays.fill(password, '\0');
    }
  }

  public static class EncryptionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public EncryptionException(String message, Throwable cause) {
      super(message, cause);
    }

    public EncryptionException(String message) {
      super(message);
    }
  }
}
