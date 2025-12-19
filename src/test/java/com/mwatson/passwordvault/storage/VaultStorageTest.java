package com.mwatson.passwordvault.storage;

import com.mwatson.passwordvault.model.Vault;
import com.mwatson.passwordvault.crypto.EncryptionService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;


public class VaultStorageTest {

  @TempDir
  Path tempDir;

  private VaultStorage vaultStorage;
  private Path testVaultFile;

  @BeforeEach
  public void setUp() {
    testVaultFile = tempDir.resolve("password-vault.dat");
    vaultStorage = new VaultStorage(testVaultFile.toString());
  }

  @Test
  public void testDebugEverything() throws Exception {
    System.err.println("=== DEBUG EVERYTHING ===");

    // 1. Create vault
    Vault vault = new Vault();
    vault.setName("Test");
    byte[] originalSalt = vault.getSalt();
    System.err
        .println("1. Original salt (Base64): " + Base64.getEncoder().encodeToString(originalSalt));
    System.err.println("1. Original salt length: " + originalSalt.length);

    // 2. Convert vault to JSON
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(vault);
    System.err.println("2. JSON to encrypt:\n" + json);

    // 3. Test encryption directly
    EncryptionService es = new EncryptionService();
    char[] password = "test123".toCharArray();

    System.err.println("3. Testing encryption directly...");
    String encrypted = es.encryptToBase64(json, password, originalSalt);
    System.err.println("4. Encrypted (first 100 chars): "
        + (encrypted.length() > 100 ? encrypted.substring(0, 100) + "..." : encrypted));

    // 4. Test decryption directly
    try {
      String decrypted = es.decryptFromBase64(encrypted, password, originalSalt);
      System.err.println("5. Decrypted JSON:\n" + decrypted);
      System.err.println("6. JSON equals: " + json.equals(decrypted));
    } catch (Exception e) {
      System.err.println("5. DIRECT DECRYPTION FAILED: " + e.getMessage());
      e.printStackTrace();
    }

    // 5. Now test through VaultStorage
    System.err.println("\n7. Testing through VaultStorage...");
    vaultStorage.saveVault(vault, password);

    // 6. Read saved file
    String fileContent = new String(Files.readAllBytes(testVaultFile), StandardCharsets.UTF_8);
    System.err.println("8. File content:\n" + fileContent);

    // 7. Parse EncryptedVault
    EncryptedVault ev = gson.fromJson(fileContent, EncryptedVault.class);
    System.err.println("9. Saved salt (Base64): " + ev.getSaltBase64());
    System.err.println("10. Encrypted data saved length: " + ev.getEncryptedDataBase64().length());
      
    // 8. Check if saved salt matches original
    byte[] savedSalt = Base64.getDecoder().decode(ev.getSaltBase64());
    System.err.println("11. Saved salt equals original: " + Arrays.equals(originalSalt, savedSalt));
      
    // 9. Try to decrypt what was saved
    try {
      String decryptedFromFile =
          es.decryptFromBase64(ev.getEncryptedDataBase64(), password, savedSalt);
      System.err.println("12. Successfully decrypted from file!");
      System.err.println("13. Decrypted JSON from file:\n" + decryptedFromFile);
    } catch (Exception e) {
      System.err.println("12. DECRYPTION FROM FILE FAILED: " + e.getMessage());
      e.printStackTrace();
    }
      
    // 10. Try loadVault
    try {
      Vault loaded = vaultStorage.loadVault(password);
      System.err.println("14. loadVault() succeeded!");
    } catch (Exception e) {
      System.err.println("14. loadVault() FAILED: " + e.getMessage());
      e.printStackTrace();
    }
      
    Arrays.fill(password, '\0');
    System.err.println("=== END DEBUG ===");
  }
}