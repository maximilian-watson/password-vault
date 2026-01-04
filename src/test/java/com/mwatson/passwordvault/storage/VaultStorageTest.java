package com.mwatson.passwordvault.storage;

import com.mwatson.passwordvault.model.PasswordEntry;
import com.mwatson.passwordvault.model.Vault;
import com.mwatson.passwordvault.crypto.EncryptionService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.security.SecureRandom;


public class VaultStorageTest {

  /*
  @Test
  public void testActualFileEncryption() throws Exception {
    System.out.println("=== TEST WITH ACTUAL FILE ===");

    // 1. Create a test file in current directory
    File testFile = new File("test_encryption_debug.vault");

    // 2. Simple test data
    String testData = "Hello World!";
    char[] password = "password123".toCharArray();
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);

    System.out.println("Test data: '" + testData + "'");
    System.out.println("Password: " + new String(password));
    System.out.println("Salt (hex): " + bytesToHex(salt));

    // 3. Encrypt
    EncryptionService encryptionService = new EncryptionService();
    byte[] encryptedWithIv = encryptionService.encrypt(testData, password, salt);

    System.out.println("\nEncrypted result:");
    System.out.println("Total bytes: " + encryptedWithIv.length);

    // Check IV (first 12 bytes)
    byte[] ivFromEncryption = new byte[12];
    System.arraycopy(encryptedWithIv, 0, ivFromEncryption, 0, 12);
    System.out.println("IV (first 12 bytes): " + bytesToHex(ivFromEncryption));

    // 4. Save to actual file (like your real code does)
    String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedWithIv);
    String saltBase64 = Base64.getEncoder().encodeToString(salt);

    String jsonContent = String.format("{\"saltBase64\":\"%s\",\"encryptedDataBase64\":\"%s\"}",
        saltBase64, encryptedBase64);

    Files.write(testFile.toPath(), jsonContent.getBytes(StandardCharsets.UTF_8));
    System.out.println("\nSaved to file: " + testFile.getAbsolutePath());
      System.out.println("File size: " + testFile.length() + " bytes");

    // 5. Read back from actual file
    System.out.println("\n=== READING BACK FROM FILE ===");
    String fileContent = new String(Files.readAllBytes(testFile.toPath()), StandardCharsets.UTF_8);
      System.out.println("File content: " + fileContent);

    // Parse JSON
    Gson gson = new Gson();
    JsonObject json = gson.fromJson(fileContent, JsonObject.class);

    String readSaltBase64 = json.get("saltBase64").getAsString();
    String readEncryptedBase64 = json.get("encryptedDataBase64").getAsString();

    byte[] readSalt = Base64.getDecoder().decode(readSaltBase64);
    byte[] readEncryptedData = Base64.getDecoder().decode(readEncryptedBase64);

    System.out.println("\nRead from file:");
    System.out.println("Salt matches original: " + Arrays.equals(salt, readSalt));
    System.out.println("Encrypted data length: " + readEncryptedData.length + " bytes");

    // Extract IV from what we read
    byte[] ivFromFile = new byte[12];
    System.arraycopy(readEncryptedData, 0, ivFromFile, 0, 12);
    System.out.println("IV from file (first 12 bytes): " + bytesToHex(ivFromFile));
    System.out.println("IV matches original: " + Arrays.equals(ivFromEncryption, ivFromFile));

    // 6. Try to decrypt
    System.out.println("\n=== ATTEMPTING DECRYPTION ===");
    try {
      String decrypted = encryptionService.decrypt(readEncryptedData, password, readSalt);
      System.out.println("Decryption SUCCESS!");
      System.out.println("Result: '" + decrypted + "'");
      System.out.println("Matches original: " + testData.equals(decrypted));
    } catch (Exception e) {
      System.out.println("Decryption FAILED!");
      System.out.println("Error: " + e.getClass().getSimpleName());
      System.out.println("Message: " + e.getMessage());
      e.printStackTrace();
      }

    // 7. Clean up
    testFile.delete();
    System.out.println("\nCleaned up test file.");
  }
  */
  
  /*
  @Test
  public void testVaultObjectEncryption() throws Exception {
    System.out.println("=== TEST VAULT OBJECT ENCRYPTION ===");

    // 1. Create a test file in current directory
    File testFile = new File("test_vault_encryption.vault");

    // 2. Create a vault object
    Vault vault = new Vault();
    vault.setName("My Test Vault");
    vault.addEntry(new PasswordEntry("google.com", "test@gmail.com", "mypassword".toCharArray()));
    vault.addEntry(new PasswordEntry("github.com", "devuser", "githubpass".toCharArray()));

    System.out.println("Vault created:");
    System.out.println("  Name: " + vault.getName());
    System.out.println("  ID: " + vault.getId());
    System.out.println("  Salt Base64: " + Base64.getEncoder().encodeToString(vault.getSalt()));
    System.out.println("  Entries: " + vault.getEntryCount());

    Gson gson = new Gson();
    String vaultJson = gson.toJson(vault);
    System.out.println("\nVault JSON length: " + vaultJson.length() + " characters");
    System.out.println("Vault JSON (first 200 chars): "
        + vaultJson.substring(0, Math.min(200, vaultJson.length())));

    char[] password = "password123".toCharArray();
    byte[] salt = vault.getSalt();

    EncryptionService encryptionService = new EncryptionService();
    byte[] encryptedWithIv = encryptionService.encrypt(vaultJson, password, salt);

    System.out.println("\nEncrypted result:");
    System.out.println("Total bytes: " + encryptedWithIv.length);

    byte[] ivFromEncryption = new byte[12];
    System.arraycopy(encryptedWithIv, 0, ivFromEncryption, 0, 12);
    System.out.println("IV (first 12 bytes): " + bytesToHex(ivFromEncryption));

    String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedWithIv);
    String saltBase64 = Base64.getEncoder().encodeToString(salt);
    String jsonContent = String.format("{\"saltBase64\":\"%s\",\"encryptedDataBase64\":\"%s\"}",
        saltBase64, encryptedBase64);

    Files.write(testFile.toPath(), jsonContent.getBytes(StandardCharsets.UTF_8));
    System.out.println("\nSaved to file: " + testFile.getAbsolutePath());
    System.out.println("File size: " + testFile.length() + " bytes");

    System.out.println("\n=== READING BACK FROM FILE ===");
    String fileContent = new String(Files.readAllBytes(testFile.toPath()), StandardCharsets.UTF_8);
    System.out.println("File content: " + fileContent);

    JsonObject json = gson.fromJson(fileContent, JsonObject.class);

    String readSaltBase64 = json.get("saltBase64").getAsString();
    String readEncryptedBase64 = json.get("encryptedDataBase64").getAsString();

    byte[] readSalt = Base64.getDecoder().decode(readSaltBase64);
    byte[] readEncryptedData = Base64.getDecoder().decode(readEncryptedBase64);

    System.out.println("\nRead from file:");
    System.out.println("Salt matches original: " + Arrays.equals(salt, readSalt));
    System.out.println("Encrypted data length: " + readEncryptedData.length + " bytes");

    byte[] ivFromFile = new byte[12];
    System.arraycopy(readEncryptedData, 0, ivFromFile, 0, 12);
    System.out.println("IV from file (first 12 bytes): " + bytesToHex(ivFromFile));
    System.out.println("IV matches original: " + Arrays.equals(ivFromEncryption, ivFromFile));

    System.out.println("\n=== ATTEMPTING DECRYPTION ===");
    try {
      String decryptedJson = encryptionService.decrypt(readEncryptedData, password, readSalt);
      System.out.println("Decryption SUCCESS!");
      System.out.println("Decrypted JSON length: " + decryptedJson.length() + " characters");
      System.out.println("Decrypted JSON (first 200 chars): "
          + decryptedJson.substring(0, Math.min(200, decryptedJson.length())));

      Vault decryptedVault = gson.fromJson(decryptedJson, Vault.class);
      System.out.println("\nParsed Vault:");
      System.out.println("  Name: " + decryptedVault.getName());
      System.out.println("  ID: " + decryptedVault.getId());
      System.out.println("  Entries: " + decryptedVault.getEntryCount());

      decryptedVault.setSalt(readSalt);
      System.out.println("  Set salt from file to vault");

      // Verify the vault matches
      System.out.println("\nVerification:");
      System.out.println("  Name matches: " + vault.getName().equals(decryptedVault.getName()));
      System.out.println("  ID matches: " + vault.getId().equals(decryptedVault.getId()));
      System.out.println(
          "  Entry count matches: " + (vault.getEntryCount() == decryptedVault.getEntryCount()));

    } catch (Exception e) {
      System.out.println("Decryption FAILED!");
      System.out.println("Error: " + e.getClass().getSimpleName());
      System.out.println("Message: " + e.getMessage());
      e.printStackTrace();
    }

    // 8. Clean up
    testFile.delete();
    System.out.println("\nCleaned up test file.");

  }
  */


  @Test
  public void testVaultStorageWorkflow() throws Exception {
    // Testing overall workflow of Vault Storage
    // Create test file
    String testFileName = "test_vaultstorage_real.dat";
    File testFile = new File(testFileName);

    if (testFile.exists()) {
      testFile.delete();
    }

    try {
      VaultStorage storage = new VaultStorage(testFileName);

      // Add new vault with password entry items
      Vault vault = new Vault();
      vault.setName("Test Vault Storage");
      vault.addEntry(new PasswordEntry("example.com", "user1", "pass123".toCharArray()));

      // Master password
      char[] password = "password123".toCharArray();

      // Save the vault with the Master Password
      storage.saveVault(vault, password);
      // variable password, which holds the Master password, is cleared in this method

      // Check file created
      assertTrue(testFile.exists(), "File should exist");

      // Read the file
      String fileContent =
          new String(Files.readAllBytes(testFile.toPath()), StandardCharsets.UTF_8);


      // Check the structure of the Json
      Gson gson = new Gson();
      JsonObject json = gson.fromJson(fileContent, JsonObject.class);
      assertTrue(json.has("saltBase64"));
      assertTrue(json.has("encryptedDataBase64"));
      String savedSaltBase64 = json.get("saltBase64").getAsString();
      String savedEncryptedBase64 = json.get("encryptedDataBase64").getAsString();

      // Decode the salt and the encrypted data
      byte[] savedSalt = Base64.getDecoder().decode(savedSaltBase64);
      byte[] savedEncrypted = Base64.getDecoder().decode(savedEncryptedBase64);

      // Check if encrypted data contains IV, 12 bytes long
      assertTrue(savedEncrypted.length >= 12);

      // Load Vault with password, variable password has been deleted
      Vault loadedVault = storage.loadVault("password123".toCharArray());

      // Assert loaded vault isn't empty
      assertNotNull(loadedVault);
      // Check matches the original vault
      assertEquals(vault.getId(), loadedVault.getId());
      assertEquals(vault.getName(), loadedVault.getName());
      assertEquals(vault.getEntryCount(), loadedVault.getEntryCount());
      assertArrayEquals(vault.getSalt(), loadedVault.getSalt());

      // Check entries
      List<PasswordEntry> originalEntries = vault.getAllEntries();
      List<PasswordEntry> loadedEntries = loadedVault.getAllEntries();

      assertEquals(originalEntries.size(), loadedEntries.size(), "Number of entries should match");
      
      if (!originalEntries.isEmpty() && !loadedEntries.isEmpty()) {
        PasswordEntry originalEntry = originalEntries.get(0);
        PasswordEntry loadedEntry = loadedEntries.get(0);

        assertEquals(originalEntry.getUsername(), loadedEntry.getUsername());
        assertEquals(originalEntry.getUrl(), loadedEntry.getUrl());
      }
    } finally {
      // Delete test file
      if (testFile.exists()) {
        testFile.delete();
      }
    }
  }
}