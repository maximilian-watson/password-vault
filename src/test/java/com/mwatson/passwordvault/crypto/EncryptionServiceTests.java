package com.mwatson.passwordvault.crypto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class EncryptionServiceTests {
  private EncryptionService encryptionService;

  @BeforeEach
  public void setUp() {
    encryptionService = new EncryptionService();
  }

  @Test
  public void testEncryptBasic() {
    // Test 1 basic encrypt() method tests
    char[] password = "masterPassword123".toCharArray();
    byte[] salt = encryptionService.generateSalt();
    String originalData = "netflixPassword";
    byte[] encrypted = encryptionService.encrypt(originalData, password, salt);
    assertNotNull(encrypted);
    assertTrue(encrypted.length > 0);

    encryptionService.clearPassword(password);
  }

  @Test
  public void testEncryptEmptyString() {
    // Test 2 check that encrypting an empty password works
    char[] password = "password".toCharArray();
    byte[] salt = encryptionService.generateSalt();
    byte[] encrypted = encryptionService.encrypt("", password, salt);
    assertNotNull(encrypted);
    assertTrue(encrypted.length > 0);

    encryptionService.clearPassword(password);
  }

  @Test
  public void testEncryptNullData() {
    // Test 3 check that correct exception is thrown when there is null parameters
    char[] password = "test".toCharArray();
    byte[] salt = new byte[16];
    String originalData = "netflixPassword";
    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.encrypt(null, password, salt));
    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.encrypt(originalData, null, salt));
    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.encrypt(originalData, password, null));
    encryptionService.clearPassword(password);
  }

  @Test
  public void testDecryptBasic() {
    // Test 4 test basic decrypt() method tests
    char[] password = "test123".toCharArray();
    byte[] salt = encryptionService.generateSalt();
    String original = "Secret message";

    byte[] encrypted = encryptionService.encrypt(original, password, salt);
    String decrypted = encryptionService.decrypt(encrypted, password, salt);
    assertEquals(original, decrypted);
    encryptionService.clearPassword(password);
  }

  @Test
  public void testDecryptWithWrongPassword() {
    // Test 5 test exception is thrown when the wrong master password is used
    char[] correctPassword = "correct".toCharArray();
    char[] wrongPassword = "wrong".toCharArray();
    byte[] salt = encryptionService.generateSalt();

    byte[] encrypted = encryptionService.encrypt("data", correctPassword, salt);

    assertThrows(EncryptionService.EncryptionException.class,
        () -> encryptionService.decrypt(encrypted, wrongPassword, salt));

    encryptionService.clearPassword(correctPassword);
    encryptionService.clearPassword(wrongPassword);
  }

  @Test
  public void testDecryptWithWrongSalt() {
    // Test 6 test exception is thrown when the wrong salt is used
    char[] password = "password".toCharArray();
    byte[] salt1 = encryptionService.generateSalt();
    byte[] salt2 = encryptionService.generateSalt();

    byte[] encrypted = encryptionService.encrypt("data", password, salt1);

    assertThrows(EncryptionService.EncryptionException.class,
        () -> encryptionService.decrypt(encrypted, password, salt2));

    encryptionService.clearPassword(password);
  }
  
  @Test
  public void testDecryptTooShortData() {
    // Test 7 test exception is thrown when encrypted data is too short less than the GCM_IV_LENGTH 12
    char[] password = "test".toCharArray();
    byte[] salt = new byte[16];
    byte[] tooShort = new byte[5]; 
    
    assertThrows(IllegalArgumentException.class, () -> 
        encryptionService.decrypt(tooShort, password, salt));
    
    encryptionService.clearPassword(password);
  }

  @Test
  public void testDecryptNullData() {
    // Test 8 test exception is thrown when the data is null
    char[] password = "test".toCharArray();
    byte[] salt = new byte[16];
    byte[] encrypted = encryptionService.encrypt("data", password, salt);

    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.decrypt(null, password, salt));
    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.decrypt(encrypted, null, salt));
    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.decrypt(encrypted, password, null));
    encryptionService.clearPassword(password);
  }

  @Test
  public void testEncryptToBase64() {
    // Test 9 verify encrypted data is in base64
    char[] password = "base64test".toCharArray();
    byte[] salt = encryptionService.generateSalt();
    String data = "Data for Base64 encryption";
    String base64Encrypted = encryptionService.encryptToBase64(data, password, salt);
    assertNotNull(base64Encrypted);

    assertDoesNotThrow(() -> Base64.getDecoder().decode(base64Encrypted));

    encryptionService.clearPassword(password);
  }

  @Test
  public void testEncryptToBase64EmptyData() {
    // Test 10 verify empty data is encrypted into base664
    char[] password = "test".toCharArray();
    byte[] salt = encryptionService.generateSalt();

    String base64 = encryptionService.encryptToBase64("", password, salt);
    assertNotNull(base64);
    assertTrue(base64.length() > 0);

    encryptionService.clearPassword(password);
  }

  @Test
  public void testDecryptFromBase64() {
    // Test 11 verify decrypted data is in base64
    char[] password = "base64password".toCharArray();
    byte[] salt = encryptionService.generateSalt();
    String original = "Test data for Base64";

    String base64Encrypted = encryptionService.encryptToBase64(original, password, salt);
    String decrypted = encryptionService.decryptFromBase64(base64Encrypted, password, salt);

    assertEquals(original, decrypted);

    encryptionService.clearPassword(password);
  }

  @Test
  public void testDecryptFromBase64InvalidBase64() {
    // Test 12 verify exception is thrown when data is not in base64
    char[] password = "test".toCharArray();
    byte[] salt = new byte[16];
    String invalidBase64 = "Not valid Base64!!!";

    assertThrows(IllegalArgumentException.class,
        () -> encryptionService.decryptFromBase64(invalidBase64, password, salt));

    encryptionService.clearPassword(password);
  }

  @Test
  public void testDecryptFromBase64WithWrongPassword() {
    // Test 13 test decryption fails with the wrong password
    char[] correctPassword = "correct".toCharArray();
    char[] wrongPassword = "wrong".toCharArray();
    byte[] salt = encryptionService.generateSalt();

    String base64Encrypted = encryptionService.encryptToBase64("data", correctPassword, salt);

    assertThrows(EncryptionService.EncryptionException.class,
        () -> encryptionService.decryptFromBase64(base64Encrypted, wrongPassword, salt));

    encryptionService.clearPassword(correctPassword);
    encryptionService.clearPassword(wrongPassword);
  }

  @Test
  public void testGenerateSalt() {
    // Test 14 test salt is generated and at the right length
    byte[] salt = encryptionService.generateSalt();
    assertEquals(16, salt.length);
    assertNotNull(salt);
  }

  @Test
  public void testGenerateSaltRandomness() {
    // Test 15 test salts generated are random
    byte[] salt1 = encryptionService.generateSalt();
    byte[] salt2 = encryptionService.generateSalt();

    assertFalse(Arrays.equals(salt1, salt2));
  }

  @Test
  public void testClearPassword() {
    // Test 16 tests password is cleared and only the password is cleared
    char[] password = "password123".toCharArray();
    char[] originalCopy = password.clone();

    encryptionService.clearPassword(password);

    for (char c : password) {
      assertEquals('\0', c);
    }

    assertArrayEquals("password123".toCharArray(), originalCopy);
    encryptionService.clearPassword(originalCopy);
  }

  @Test
  public void testClearPasswordNull() {
    // Test 17 tests exception not thrown when clear a null password
    assertDoesNotThrow(() -> encryptionService.clearPassword(null));
  }

  @Test
  public void testEncryptionExceptionSingleParmConstructor() {
    // Test 18 test the single parameter constructor
    String message = "Test error message";
    EncryptionService.EncryptionException exception =
        new EncryptionService.EncryptionException(message);

    assertEquals(message, exception.getMessage());
  }
}
