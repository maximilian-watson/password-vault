package com.mwatson.passwordvault.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;


public class PasswordEntryTest {
  private PasswordEntry entry;

  @BeforeEach
  public void setUp() {
    entry = new PasswordEntry();
  }

  @Test
  public void testDefaultConstructor() {
    // Test 1
    assertNotNull(entry.getId());
    assertTrue(entry.getId().length() > 10);
    assertEquals("General", entry.getCategory());
    assertNotNull(entry.getCreatedAt());
    assertNotNull(entry.getUpdatedAt());
    assertEquals("Untitled Entry", entry.getDisplayName());
  }

  @Test
  public void testConstructorWithThreeParams() {
    // Test 2
    char[] password = "password123".toCharArray();
    PasswordEntry entry = new PasswordEntry("Gmail", "user@gmail.com", password);

    assertEquals("Gmail", entry.getTitle());
    assertEquals("user@gmail.com", entry.getUsername());
    assertArrayEquals(password, entry.getPassword());
    Arrays.fill(password, '\0');
  }

  @Test
  public void testFullConstructor() {
    // Test 3
    char[] password = "netflix123".toCharArray();
    PasswordEntry entry = new PasswordEntry("Netflix", "john@email.com", password,
        "https://netflix.com", "Family account", "Entertainment");
    assertEquals("Netflix", entry.getTitle());
    assertEquals("john@email.com", entry.getUsername());
    assertArrayEquals(password, entry.getPassword());
    assertEquals("https://netflix.com", entry.getUrl());
    assertEquals("Family account", entry.getNotes());
    assertEquals("Entertainment", entry.getCategory());
    Arrays.fill(password, '\0');
  }

  @Test
  public void testPasswordSetterGetter() {
    // Test 4
    char[] password1 = "firstPassword".toCharArray();
    char[] password2 = "secondPassword".toCharArray();
    entry.setPassword(password1);
    assertArrayEquals(password1, entry.getPassword());

    // Setting new password should clear old one
    entry.setPassword(password2);
    assertArrayEquals(password2, entry.getPassword());
    Arrays.fill(password1, '\0');
    Arrays.fill(password2, '\0');
  }

  @Test
  public void testMatchesSearch() {
    // Test 5
    char[] password = "pass".toCharArray();
    PasswordEntry entry = new PasswordEntry("GitHub", "dev@github.com", password,
        "https://github.com", "Work repository", "Development");
    assertTrue(entry.matchesSearch("github"));
    assertTrue(entry.matchesSearch("GITHUB")); // Case insensitive
    assertTrue(entry.matchesSearch("dev@github"));
    assertTrue(entry.matchesSearch("https://github.com"));
    assertFalse(entry.matchesSearch("https://GIThub.uk"));

    assertTrue(entry.matchesSearch("work"));
    assertTrue(entry.matchesSearch("git"));
    assertTrue(entry.matchesSearch(null));
    assertTrue(entry.matchesSearch(""));
    assertTrue(entry.matchesSearch("   "));
    assertTrue(entry.matchesSearch("\t\n"));

    assertFalse(entry.matchesSearch("amazon"));
    Arrays.fill(password, '\0');
  }

  @Test
  public void testToString() {
    // Test 6
    char[] password = "pass".toCharArray();
    entry = new PasswordEntry("Facebook", "max@fb.com", password);
    assertEquals("Facebook (max@fb.com)", entry.toString());
    entry.setUsername(null);
    assertEquals("Facebook (no username)", entry.toString());
    Arrays.fill(password, '\0');
  }

  @Test
  public void testSettersUpdatesTimestamp() throws InterruptedException {
    // Test 7
    LocalDateTime initialTime = entry.getUpdatedAt();
    Thread.sleep(10);
    char[] password = "newpass".toCharArray();
    entry.setPassword(password);
    assertTrue(entry.getUpdatedAt().isAfter(initialTime));
    Arrays.fill(password, '\0');
  }

  @Test
  public void testGetDisplayName() {
    // Test 8
    entry.setTitle(null);
    assertEquals("Untitled Entry", entry.getDisplayName());

    entry.setTitle("");
    assertEquals("Untitled Entry", entry.getDisplayName());

    entry.setTitle("   ");
    assertEquals("Untitled Entry", entry.getDisplayName());

    entry.setTitle("Aikatsu");
    assertEquals("Aikatsu", entry.getDisplayName());
  }

  @Test
  public void testMatchesSearchWithNullFields() {
    // Test 9
    assertTrue(entry.matchesSearch(null));
    assertTrue(entry.matchesSearch(""));
    assertFalse(entry.matchesSearch("anything"));
  }

  @Test
  public void testSetUrl() {
    // Test 10
    entry.setUrl("vndb.org");
    assertEquals("vndb.org", entry.getUrl());
    entry.setUrl("");
    assertEquals("", entry.getUrl());
  }

  @Test
  public void testSetNotes() {
    // Test 11
    entry.setNotes("Important account");
    assertEquals("Important account", entry.getNotes());
    entry.setNotes(null);
    assertNull(entry.getNotes());
  }

  @Test
  public void testSetCategory() {
    // Test 12
    entry.setCategory("Work");
    assertEquals("Work", entry.getCategory());
    entry.setCategory(null);
    assertNull(entry.getCategory());
  }
  
  @Test
  public void testGetCreatedAtFormatted() {
    // Test 13
    String formatted = entry.getCreatedAtFormatted();
    assertNotNull(formatted);
    assertTrue(formatted.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    LocalDateTime specificDate = LocalDateTime.of(2023, 5, 22, 12, 15, 50);
    entry.setCreatedAt(specificDate);
    assertEquals("22-05-2023 12:15:50", entry.getCreatedAtFormatted());
  }

  @Test
  public void testGetUpdatedAtFormatted() throws InterruptedException {
    // Test 14
    String formatted = entry.getUpdatedAtFormatted();
    assertNotNull(formatted);
    assertTrue(formatted.matches("\\d{2}-\\d{2}-\\d{4} \\d{2}:\\d{2}:\\d{2}"));
    Thread.sleep(1100);
    entry.setTitle("Updated");
    String newFormatted = entry.getUpdatedAtFormatted();
    assertNotEquals(formatted, newFormatted);

    LocalDateTime specificDate = LocalDateTime.of(2024, 4, 15, 9, 15, 30);
    entry.setUpdatedAt(specificDate);
    assertEquals("15-04-2024 09:15:30", entry.getUpdatedAtFormatted());
  }

  @Test
  public void testSetId() {
    // Test 15
    entry.setId("123");
    assertEquals("123", entry.getId());
    entry.setId(null);
    assertNull(entry.getId());
  }

  @Test
  public void testGetDateFormatter() {
    // Test 16
    assertNotNull(PasswordEntry.getDateFormatter());
    LocalDateTime testDate = LocalDateTime.of(2023, 12, 25, 14, 30, 45);
    String formatted = testDate.format(PasswordEntry.getDateFormatter());
    assertEquals("25-12-2023 14:30:45", formatted);
  }
}
