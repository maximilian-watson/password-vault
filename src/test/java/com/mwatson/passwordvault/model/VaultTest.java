package com.mwatson.passwordvault.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VaultTest {
  private Vault vault;
  private PasswordEntry entry1;
  private PasswordEntry entry2;

  @BeforeEach
  public void setUp() {
    vault = new Vault();
    char[] password1 = "pass1".toCharArray();
    char[] password2 = "pass2".toCharArray();

    entry1 = new PasswordEntry("Gmail", "user1@gmail.com", password1, "https://gmail.com",
        "work email", "Email");
    entry2 = new PasswordEntry("GitHub", "dev@github.com", password2, "https://github.com",
        "Personal Account", "Development");

    Arrays.fill(password1, '\0');
    Arrays.fill(password2, '\0');
  }

  @Test
  public void testVaultCreation() {
    // Test 1 test vault is created correctly and initialised with the right values
    assertNotNull(vault.getId());
    assertEquals("My Password Vault", vault.getName());
    assertEquals(0, vault.getEntryCount());
    assertNotNull(vault.getSalt());
    assertEquals(16, vault.getSalt().length);
  }

  @Test
  public void testAddEntry() {
    // Test 2 test entries are added
    vault.addEntry(entry1);
    assertEquals(1, vault.getEntryCount());

    vault.addEntry(entry2);
    assertEquals(2, vault.getEntryCount());
  }

  @Test
  public void testRemoveEntry() {
    // Test 3 test entries are removed and entry Ids not in the vault aren't removed
    vault.addEntry(entry1);
    vault.addEntry(entry2);

    assertTrue(vault.removeEntry(entry1.getId()));
    assertEquals(1, vault.getEntryCount());

    assertFalse(vault.removeEntry("non_existent_id"));
    assertEquals(1, vault.getEntryCount());
  }

  @Test
  public void testGetEntry() {
    // Test 4 test getting added entries
    vault.addEntry(entry1);

    PasswordEntry found = vault.getEntry(entry1.getId());
    assertNotNull(found);
    assertEquals(entry1.getId(), found.getId());
    assertEquals("Gmail", found.getTitle());

    assertNull(vault.getEntry("nonexistent-id"));
  }

  @Test
  public void testGetAllEntries() {
    // Test 5 test getting all the entries has all the added entries
    vault.addEntry(entry1);
    vault.addEntry(entry2);

    List<PasswordEntry> allEntries = vault.getAllEntries();
    assertEquals(2, allEntries.size());
    assertTrue(allEntries.contains(entry1));
    assertTrue(allEntries.contains(entry2));
  }

  @Test
  public void testSearch() {
    // Test 6 test searching all relevant fields of password entry
    vault.addEntry(entry1);
    vault.addEntry(entry2);

    List<PasswordEntry> results = vault.search("gmail");
    assertEquals(1, results.size());
    assertEquals("Gmail", results.get(0).getTitle());

    results = vault.search("dev@github.com");
    assertEquals(1, results.size());
    assertEquals("GitHub", results.get(0).getTitle());

    results = vault.search("github.com");
    assertEquals(1, results.size());

    results = vault.search("work");
    assertEquals(1, results.size());

    results = vault.search("");
    assertEquals(2, results.size());

    results = vault.search(null);
    assertEquals(2, results.size());

    results = vault.search("nonexistent text");
    assertEquals(0, results.size());
  }

  @Test
  public void testGetEntriesByCategory() {
    // Test 7 testing that we can get entries by category
    vault.addEntry(entry1);
    vault.addEntry(entry2);

    List<PasswordEntry> emailEntries = vault.getEntriesByCategory("Email");
    assertEquals(1, emailEntries.size());
    assertEquals("Gmail", emailEntries.get(0).getTitle());

    List<PasswordEntry> devEntries = vault.getEntriesByCategory("Development");
    assertEquals(1, devEntries.size());
    assertEquals("GitHub", devEntries.get(0).getTitle());

    List<PasswordEntry> empty = vault.getEntriesByCategory("Nonexistent");
    assertEquals(0, empty.size());
  }

  @Test
  public void testGetCategories() {
    // Test 8 testing we can get all categories in the vault with no duplicates
    vault.addEntry(entry1);
    vault.addEntry(entry2);
    char[] password3 = "pass3".toCharArray();
    PasswordEntry entry3 = new PasswordEntry("GitLab", "dev@gitLab.com", password3,
        "https://gitlab.com", "Work Account", "Development");
    vault.addEntry(entry3);
    List<String> categories = vault.getCategories();
    assertEquals(2, categories.size());
    assertTrue(categories.contains("Email"));
    assertTrue(categories.contains("Development"));
  }

  @Test
  public void testClear() {
    // Test 9 testing clear passwords from the vault
    vault.addEntry(entry1);
    vault.addEntry(entry2);
    assertEquals(2, vault.getEntryCount());

    vault.clear();
    assertEquals(0, vault.getEntryCount());
    assertTrue(vault.getAllEntries().isEmpty());
  }

  @Test
  public void testSetters() {
    // Test 10 testing setters
    vault.setName("Personal Vault");
    assertEquals("Personal Vault", vault.getName());

    byte[] newSalt = new byte[16];
    Arrays.fill(newSalt, (byte) 1);
    vault.setSalt(newSalt);
    assertArrayEquals(newSalt, vault.getSalt());

    // original array shouldn't be affected if the new salt is modified
    newSalt[0] = (byte) 2;
    assertNotEquals(newSalt[0], vault.getSalt()[0]);
  }

  @Test
  public void testVaultConstructorWithExistingData() {
    // Test 11 full test for the the second constructor, with existing data check all assigned
    // correctly and retrieved
    String expectedId = "test-vault-id-123";
    String expectedName = "My Personal Vault";
    char[] password1 = "pass1".toCharArray();
    char[] password2 = "pass2".toCharArray();
    PasswordEntry entry1 = new PasswordEntry("Gmail", "user1@gmail.com", password1);
    PasswordEntry entry2 = new PasswordEntry("Netflix", "user2@netflix.com", password2);
    List<PasswordEntry> entries = new ArrayList<>();
    entries.add(entry1);
    entries.add(entry2);
    byte[] expectedSalt = new byte[16];
    Arrays.fill(expectedSalt, (byte) 42);
    Vault vault = new Vault(expectedId, expectedName, entries, expectedSalt);
    assertEquals(expectedId, vault.getId());
    assertEquals(expectedName, vault.getName());
    assertArrayEquals(expectedSalt, vault.getSalt());
    List<PasswordEntry> retrievedEntries = vault.getAllEntries();
    assertEquals(2, retrievedEntries.size());
    assertEquals("Gmail", retrievedEntries.get(0).getTitle());
    assertEquals("Netflix", retrievedEntries.get(1).getTitle());
    entries.clear();
    assertEquals(2, vault.getEntryCount());
    Arrays.fill(password1, '\0');
    Arrays.fill(password2, '\0');
  }
}
