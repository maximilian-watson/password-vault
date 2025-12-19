package com.mwatson.passwordvault.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mwatson.passwordvault.crypto.EncryptionService;
import com.mwatson.passwordvault.model.Vault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Handles saving and loading encrypted vault to and from disk.
 * Uses AES-GCM encryption with the master password.
 */
public class VaultStorage {
  private static final String VAULT_FILE_NAME = "password-vault.dat";
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private final EncryptionService encryptionService;
  private final Path vaultFilePath;

  /**
   * Default VaultStorage constructor, create new encryptionService.
   * Sets the vault file path to user's home directory.
   */
  public VaultStorage() {
    this.encryptionService = new EncryptionService();
    this.vaultFilePath = Paths.get(System.getProperty("user.home"), VAULT_FILE_NAME);
  }

  /**
   * Constructor for vault storage, chosen path to file passed in.
   *
   * @param customPath passed in
   */
  public VaultStorage(String customPath) {
    this.encryptionService = new EncryptionService();
    this.vaultFilePath = Paths.get(customPath);
  }

  /**
   * Save the vault encrypted with the master password.
   *
   * @param vault being saved
   * @param masterPassword used to encrypt
   * @throws IOException if there is an error, not handled in this function, caller must handle
   */
  public void saveVault(Vault vault, char[] masterPassword) throws IOException {
    if (vault == null || masterPassword == null) {
      throw new IllegalArgumentException("Vault and Password cannot be null");
    }
    try {
      // Convert to JSON
      String json = GSON.toJson(vault);
      // Get salt from vault
      byte[] salt = vault.getSalt();
      // Encrypt
      String encryptedBase64 = encryptionService.encryptToBase64(json, masterPassword, salt);
      // Create wrapper with the salt
      EncryptedVault encryptedVault =
          new EncryptedVault(Base64.getEncoder().encodeToString(salt), encryptedBase64);
      // Save to file
      String fileContent = GSON.toJson(encryptedVault);
      Files.write(vaultFilePath, fileContent.getBytes(StandardCharsets.UTF_8));
    } finally {
      encryptionService.clearPassword(masterPassword);
    }
  }

  /**
   * Loads a vault from the disk.
   *
   * @param masterPassword used to decrypt
   * @return the vault stored and encrypted by the masterPassword
   * @throws IOException thrown if error reading, parsing or decrypting the vault file
   */
  public Vault loadVault(char[] masterPassword) throws IOException {
    if (masterPassword == null) {
      throw new IllegalArgumentException("Master password cannot be null");
    }

    if (!vaultFileExists()) {
      return null;
    }
    try {
      // Read file
      String fileContent = new String(Files.readAllBytes(vaultFilePath), StandardCharsets.UTF_8);
      // Parse encrypted vault wrapper
      EncryptedVault encryptedVault = GSON.fromJson(fileContent, EncryptedVault.class);
      // Decode the salt
      byte[] salt = Base64.getDecoder().decode(encryptedVault.getSaltBase64());
      // decrypt the data
      String json = encryptionService.decryptFromBase64(encryptedVault.getEncryptedDataBase64(),
          masterPassword, salt);
      // Parse vault
      return GSON.fromJson(json, Vault.class);
    } catch (Exception e) {
      throw new IOException("Failed to load vault: " + e.getMessage(), e);
    } finally {
      encryptionService.clearPassword(masterPassword);
    }
  }

  /**
   * Check if the vault file exists.
   *
   * @return boolean value
   */
  public boolean vaultFileExists() {
    return Files.exists(vaultFilePath);
  }
}
