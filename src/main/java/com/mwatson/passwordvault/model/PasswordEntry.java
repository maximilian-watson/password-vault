package com.mwatson.passwordvault.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single password entry in the vault.
 * Contains all information needed to store and retrieve a password.
 */
public class PasswordEntry {
  private String id;
  private String title;
  private String username;
  private char[] password;
  private String url;
  private String notes;
  private String category;
  private transient LocalDateTime createdAt;
  private transient LocalDateTime updatedAt;

  private static final transient DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

  /**
   * Default constructor.
   */
  public PasswordEntry() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.category = "General";
  }

  /**
   * Constructor with basic fields.
   *
   * @param title password entry title
   * @param username or email
   * @param password the password
   */
  public PasswordEntry(String title, String username, char[] password) {
    this();
    this.title = title;
    this.username = username;
    this.password = password;
  }

  /**
   * Full constructor with all the fields.
   *
   * @param title entry title
   * @param username / email
   * @param password the password
   * @param url the website URL
   * @param notes additional information
   * @param category entry category / type
   */
  public PasswordEntry(String title, String username, char[] password, String url, String notes,
      String category) {
    this(title, username, password);
    this.url = url;
    this.notes = notes;
    this.category = category;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  /**
   * Set title of the password entry, also updated the updatedAt timestamp.
   *
   * @param title set a title
   */
  public void setTitle(String title) {
    this.title = title;
    this.updatedAt = LocalDateTime.now();
  }

  public String getUsername() {
    return username;
  }

  /**
   * Set the password username, update the updatedAt timestamp.
   *
   * @param username set a value to
   */
  public void setUsername(String username) {
    this.username = username;
    this.updatedAt = LocalDateTime.now();
  }

  public char[] getPassword() {
    return password;
  }

  /**
   * Set password and update updatedAt timestamp.
   *
   * @param password being set
   */
  public void setPassword(char[] password) {
    this.password = password;
    this.updatedAt = LocalDateTime.now();
  }

  public String getUrl() {
    return url;
  }

  /**
   * Set url and update updatedAt timestamp.
   *
   * @param url being set
   */
  public void setUrl(String url) {
    this.url = url;
    this.updatedAt = LocalDateTime.now();
  }

  public String getNotes() {
    return notes;
  }

  /**
   * Set notes, update updatedAt timestamp.
   *
   * @param notes being set
   */
  public void setNotes(String notes) {
    this.notes = notes;
    this.updatedAt = LocalDateTime.now();
  }

  public String getCategory() {
    return category;
  }

  /**
   * Set category, update updatedAt timestamp.
   *
   * @param category being set
   */
  public void setCategory(String category) {
    this.category = category;
    this.updatedAt = LocalDateTime.now();
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getCreatedAtFormatted() {
    return createdAt.format(DATE_FORMATTER);
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getUpdatedAtFormatted() {
    return updatedAt.format(DATE_FORMATTER);
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public static DateTimeFormatter getDateFormatter() {
    return DATE_FORMATTER;
  }

  /**
   * Gets the display name for this entry.
   *
   * @return display name
   */
  public String getDisplayName() {
    if (title != null && !title.trim().isEmpty()) {
      return title;
    } else {
      return "Untitled Entry";
    }
  }

  /**
   * Checks if this entry matches the search text.
   * Searches in title, username, URL, and notes.
   *
   * @param searchText text to search for
   * @return true if any field contains the search text
   */
  public boolean matchesSearch(String searchText) {
    if (searchText == null || searchText.trim().isEmpty()) {
      return true;
    }
    String searchLower = searchText.toLowerCase();
    return (title != null && title.toLowerCase().contains(searchLower))
        || (username != null && username.toLowerCase().contains(searchLower))
        || (url != null && url.toLowerCase().contains(searchLower))
        || (notes != null && notes.toLowerCase().contains(searchLower));
  }


  @Override
  public String toString() {
    String displayUsername;
    if (username != null) {
      displayUsername = username;
    } else {
      displayUsername = "no username";
    }
    return String.format("%s (%s)", getDisplayName(), displayUsername);
  }
}
