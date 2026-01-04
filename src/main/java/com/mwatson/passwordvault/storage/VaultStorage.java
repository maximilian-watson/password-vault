package com.mwatson.passwordvault.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mwatson.passwordvault.crypto.EncryptionService;
import com.mwatson.passwordvault.model.Vault;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

/**
 * Handles saving and loading encrypted vault to and from disk.
 * Uses AES-GCM encryption with the master password.
 */
public class VaultStorage {
  private static final String VAULT_FILE_NAME = "password-vault.dat";


  private static final Gson GSON = new GsonBuilder().create();


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
      Gson gson = new Gson();
      String vaultJson = gson.toJson(vault);
      // Get salt from vault
      byte[] salt = vault.getSalt();
      // Encrypt
      EncryptionService encryptionService = new EncryptionService();
      byte[] encrypted = encryptionService.encrypt(vaultJson, masterPassword, salt);

      String encryptedBase64 = Base64.getEncoder().encodeToString(encrypted);
      String saltBase64 = Base64.getEncoder().encodeToString(salt);
      String jsonContent = String.format("{\"saltBase64\":\"%s\",\"encryptedDataBase64\":\"%s\"}",
          saltBase64, encryptedBase64);
      Files.write(vaultFilePath, jsonContent.getBytes(StandardCharsets.UTF_8));
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

      Gson gson = new Gson();
      JsonObject json = gson.fromJson(fileContent, JsonObject.class);

      String readSaltBase64 = json.get("saltBase64").getAsString();
      String readEncryptedBase64 = json.get("encryptedDataBase64").getAsString();

      byte[] readSalt = Base64.getDecoder().decode(readSaltBase64);
      byte[] readEncryptedData = Base64.getDecoder().decode(readEncryptedBase64);

      String decryptedJson =
          encryptionService.decrypt(readEncryptedData, masterPassword, readSalt);
      Vault decryptedVault = gson.fromJson(decryptedJson, Vault.class);

      decryptedVault.setSalt(readSalt);

      return decryptedVault;

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

  /**
   * Delete the vault file.
   *
   * @throws IOException if there is an error deleting, calling program has to deal with it
   */
  public void deleteVaultFile() throws IOException {
    Files.deleteIfExists(vaultFilePath);
  }

  /**
   * Getter for vaultFilePath.
   *
   * @return the vaultFilePath as a String
   */
  public String getVaultFilePath() {
    return vaultFilePath.toString();
  }

}
