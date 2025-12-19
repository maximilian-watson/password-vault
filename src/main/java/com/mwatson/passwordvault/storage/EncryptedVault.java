package com.mwatson.passwordvault.storage;

/**
 * Container for encrypted vault data that includes the salt.
 * This is what gets written to the disk.
 */
public class EncryptedVault {
  private String saltBase64;
  private String encryptedDataBase64;

  public EncryptedVault() {
  }

  public EncryptedVault(String saltBase64, String encryptedDataBase64) {
    this.saltBase64 = saltBase64;
    this.encryptedDataBase64 = encryptedDataBase64;
  }

  public String getSaltBase64() {
    return saltBase64;
  }

  public void setSaltBase64(String saltBase64) {
    this.saltBase64 = saltBase64;
  }

  public String getEncryptedDataBase64() {
    return encryptedDataBase64;
  }

  public void setEncryptedDataBase64(String encryptedDataBase64) {
    this.encryptedDataBase64 = encryptedDataBase64;
  }
}
