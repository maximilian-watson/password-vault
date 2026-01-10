package com.mwatson.passwordvault.storage;

import com.mwatson.passwordvault.model.PasswordEntry;
import com.mwatson.passwordvault.model.Vault;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;


public class VaultStorageTest {
  @Test
  public void testVaultStorageWorkflow() throws Exception {
    // Test 1
    // Testing overall workflow of Vault Storage
    // Create test file
    String testFileName = "test_vaultstorage_real.dat";
    File testFile = new File(testFileName);

    if (testFile.exists()) {
      testFile.delete();
    }

    VaultStorage storage = null;
    try {
      storage = new VaultStorage(testFileName);

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
      String savedEncryptedBase64 = json.get("encryptedDataBase64").getAsString();

      // Decode the salt and the encrypted data
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
        assertArrayEquals(originalEntry.getPassword(), loadedEntry.getPassword());
      }
    } finally {
      // Delete test file
      storage.deleteVaultFile();
    }
  }
  
  @Test
  public void testEmptyConstructor() {
    // Test 2
    // Testing the VaultStorage empty constructor, test the path used is right
    VaultStorage storage = new VaultStorage();
    assertNotNull(storage);
    String expectedPath = System.getProperty("user.home") + "/password-vault.dat";
    assertEquals(expectedPath, storage.getVaultFilePath());
    // Delete file
    new File(expectedPath).delete();
  }

  @Test
  public void testSaveVaultThrowsNullVault() {
    // Test 3
    // Test the missing branch if vault passed in is null
    // Check error message too
    VaultStorage storage = new VaultStorage("test_vault_storage");
    char[] password = "test".toCharArray();
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> storage.saveVault(null, password));
    assertTrue(e.getMessage() == "Vault and Password cannot be null");
    assertFalse(new File("test_vault_storage").exists());
  }

  @Test
  public void testSaveVaultThrowsNullPassword() {
    // Test 4
    // Test the missing branch if password is null, checking error message too
    VaultStorage storage = new VaultStorage("test_vault_storage");
    Vault vault = new Vault();
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> storage.saveVault(vault, null));
    assertTrue(e.getMessage() == "Vault and Password cannot be null");
    assertFalse(new File("test_vault_storage").exists());
  }

  @Test
  public void testLoadVaultThrowsNullPassword() {
    // Test 5
    // Test missing branch in load vault when the master password is null exception is thrown
    VaultStorage storage = new VaultStorage("test_vault_storage");
    IllegalArgumentException e =
        assertThrows(IllegalArgumentException.class, () -> storage.loadVault(null));
    assertTrue(e.getMessage() == "Master password cannot be null");
  }

  @Test
  public void testLoadVaultThrowsMissingFile() throws IOException {
    // Test 6
    // Testing the missing branch in load vault when file doesn't exist
    VaultStorage storage = new VaultStorage("test_vault_storage");
    assertFalse(new File("test_vault_storage").exists());
    Vault result = storage.loadVault("test_password".toCharArray());
    // Returned vault is null when file doesn't exist
    assertNull(result);
  }
}