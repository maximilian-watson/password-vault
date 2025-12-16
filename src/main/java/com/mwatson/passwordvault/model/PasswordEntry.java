package com.mwatson.passwordvault.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PasswordEntry {
  private String id;
  private String title;
  private String username;
  private String password;
  private String url;
  private String notes;
  private String category;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

  public PasswordEntry() {
    this.id = UUID.randomUUID().toString();
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.category = "General";
  }

  public PasswordEntry(String title, String username, String password) {
    this();
    this.title = title;
    this.username = username;
    this.password = password;
  }

  public PasswordEntry(String title, String username, String password, String url, String notes,
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

  public void setTitle(String title) {
    this.title = title;
    this.updatedAt = LocalDateTime.now();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
    this.updatedAt = LocalDateTime.now();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
    this.updatedAt = LocalDateTime.now();
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
    this.updatedAt = LocalDateTime.now();
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
    this.updatedAt = LocalDateTime.now();
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
    this.updatedAt = LocalDateTime.now();
  }

  public String getCreatedAtFormatted() {
    return createdAt.format(DATE_FORMATTER);
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
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

  public String getDisplayName() {
    if (title != null && !title.isEmpty()) {
      return title;
    } else {
      return "Untitled Entry";
    }
  }

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
    if(username != null) {
      displayUsername = username;
    } else {
      displayUsername = "no username";
    }
    return String.format("%s (%s)", getDisplayName(), displayUsername);
  }
}
