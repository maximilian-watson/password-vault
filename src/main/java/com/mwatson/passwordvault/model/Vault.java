package com.mwatson.passwordvault.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Represents the entire password vault containing multiple password entries.
 * Manages the collection and provides search and filter functionality.
 */
public class Vault {
  private String id;
  private String name;
  private List<PasswordEntry> entries;
  private byte[] salt;

  /**
   * Creates new empty vault with random salt.
   */
  public Vault() {
    this.id = UUID.randomUUID().toString();
    this.name = "My Password Vault";
    this.entries = new ArrayList<>();
    this.salt = generateSalt();
  }

  /**
   * Creates a vault with existing data, for loading from storage.
   */
  public Vault(String id, String name, List<PasswordEntry> entries, byte[] salt) {
    this.id = id;
    this.name = name;
    this.entries = new ArrayList<>(entries);
    this.salt = salt;
  }

  /**
   * Adds a new password entry to the vault.
   *
   * @param entry to be added
   */
  public void addEntry(PasswordEntry entry) {
    entries.add(entry);
  }

  /**
   * Removes a password entry by its ID.
   *
   * @param entryId to remove
   * @return true if successfully removed, false if not
   */
  public boolean removeEntry(String entryId) {
    for (int i = 0; i < entries.size(); i++) {
      if (entries.get(i).getId().equals(entryId)) {
        entries.remove(i);
        return true;
      }
    }
    return false;
  }

  /**
   * Gets an entry by its Id.
   *
   * @param entryId the entry to get
   * @return the entry, null if not found
   */
  public PasswordEntry getEntry(String entryId) {
    for (PasswordEntry entry : entries) {
      if (entry.getId().equals(entryId)) {
        return entry;
      }
    }
    return null;
  }

  /**
   * Return all entries.
   *
   * @return ArrayList copy for safety
   */
  public List<PasswordEntry> getAllEntries() {
    return new ArrayList<>(entries);
  }

  /**
   * Searches all entries.
   *
   * @param searchText text that is being searched
   * @return all the matched searches
   */
  public List<PasswordEntry> search(String searchText) {
    if (searchText == null || searchText.trim().isEmpty()) {
      return getAllEntries();
    }

    List<PasswordEntry> results = new ArrayList<>();
    for (PasswordEntry entry : entries) {
      if (entry.matchesSearch(searchText)) {
        results.add(entry);
      }
    }
    return results;
  }

  /**
   * Get entries by category.
   *
   * @param category searching for
   * @return matched entries
   */
  public List<PasswordEntry> getEntriesByCategory(String category) {
    List<PasswordEntry> results = new ArrayList<>();

    for (PasswordEntry entry : entries) {
      if (category.equals(entry.getCategory())) {
        results.add(entry);
      }
    }
    return results;
  }

  /**
   * Gets all unique categories in the vault.
   *
   * @return all unique categories as a List
   */
  public List<String> getCategories() {
    List<String> categories = new ArrayList<>();
    for (PasswordEntry entry : entries) {
      String category = entry.getCategory();
      if (!categories.contains(category)) {
        categories.add(category);
      }
    }
    Collections.sort(categories);
    return categories;
  }

  /**
   * Returns number of entries in the vault.
   *
   * @return number of entries
   */
  public int getEntryCount() {
    return entries.size();
  }

  /**
   * Clears all password entries.
   */
  public void clear() {
    entries.clear();
  }

  /**
   * Get id.
   *
   * @return id
   */
  public String getId() {
    return id;
  }

  /**
   * Get vault name.
   *
   * @return name of vault
   */
  public String getName() {
    return name;
  }

  /**
   * Set vault name.
   *
   * @param name new name of vault
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the salt.
   *
   * @return clone of salt for safety
   */
  public byte[] getSalt() {
    return salt.clone();
  }

  /**
   * Set the salt.
   *
   * @param salt to set too
   */
  public void setSalt(byte[] salt) {
    this.salt = salt.clone();
  }

  /**
   * Private method to generate salt.
   *
   * @return the generated salt
   */
  private byte[] generateSalt() {
    byte[] salt = new byte[16];
    new java.security.SecureRandom().nextBytes(salt);
    return salt;
  }
}
