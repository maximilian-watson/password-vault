package com.mwatson.passwordvault.gui;

import com.mwatson.passwordvault.model.PasswordEntry;
import com.mwatson.passwordvault.model.Vault;
import com.mwatson.passwordvault.storage.VaultStorage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Main application window that opens when vault is unlocked.
 */
public class MainWindow extends JFrame {
  private static final long serialVersionUID = 1L;
  private final Vault vault;
  private JTable passwordTable;
  private PasswordTableModel tableModel;
  private JTextField searchField;
  private final VaultStorage vaultStorage;
  private final char[] masterPassword;
  private JLabel vaultLabel;

  /**
   * Saves vault to a field, builds the window, fills the table with entries.
   *
   * @param vault receives the unlocked vault
   */
  public MainWindow(Vault vault, VaultStorage vaultStorage, char[] masterPassword) {
    this.vault = vault;
    this.vaultStorage = vaultStorage;
    this.masterPassword = masterPassword;

    setUpUi();
    loadPasswordEntries();
    setVisible(true);
  }

  /**
   * Builds the window layout.
   */
  private void setUpUi() {
    setTitle("Password Vault - " + vault.getName());
    setSize(900, 600);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Main panel
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Header with vault info and search
    mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

    // Center password table
    mainPanel.add(createTablePanel(), BorderLayout.CENTER);

    // Bottom actions buttons
    mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

    add(mainPanel);

    setJMenuBar(createMenuBar());
  }

  /**
   * Left side shows vault name and number of entries, Right side has the search
   * box.
   *
   * @return the header
   */
  private JPanel createHeaderPanel() {
    JPanel headerPanel = new JPanel(new BorderLayout(10, 10));

    // Vault info on left
    vaultLabel = new JLabel(vault.getName() + " (" + vault.getEntryCount() + " entries)");
    vaultLabel.setFont(new Font("Arial", Font.BOLD, 16));
    headerPanel.add(vaultLabel, BorderLayout.WEST);

    // Search bar on right
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    searchPanel.add(new JLabel("Search:"));
    searchField = new JTextField(20);
    searchField.addKeyListener(new KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        filterTable(searchField.getText());
      }
    });
    searchPanel.add(searchField);
    headerPanel.add(searchPanel, BorderLayout.EAST);

    return headerPanel;
  }

  /**
   * Creates the PasswordTable model.
   *
   * @return the table
   */
  private JPanel createTablePanel() {

    // Create table model
    tableModel = new PasswordTableModel();
    passwordTable = new JTable(tableModel);

    // Table customised
    passwordTable.setRowHeight(30);
    passwordTable.getColumnModel().getColumn(3).setCellRenderer(new PasswordCellRenderer());
    passwordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // Add double-click listener to edit entries
    passwordTable.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          editSelectedEntry();
        }
      }
    });

    JPanel tablePanel = new JPanel(new BorderLayout());

    // Put table in scroll pane
    JScrollPane scrollPane = new JScrollPane(passwordTable);
    tablePanel.add(scrollPane, BorderLayout.CENTER);

    return tablePanel;
  }

  /**
   * Creates buttons for adding, editting, deleting, copying, refreshing.
   *
   * @return the button panel
   */
  private JPanel createButtonPanel() {

    JButton addButton = new JButton("Add Entry");
    addButton.addActionListener(e -> addNewEntry());

    JButton editButton = new JButton("Edit Selected");
    editButton.addActionListener(e -> editSelectedEntry());

    JButton deleteButton = new JButton("Delete Selected");
    deleteButton.addActionListener(e -> deleteSelectedEntry());

    JButton copyButton = new JButton("Copy Password");
    copyButton.addActionListener(e -> copyPasswordToClipboard());

    JButton refreshButton = new JButton("Refresh");
    refreshButton.addActionListener(e -> loadPasswordEntries());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);
    buttonPanel.add(copyButton);
    buttonPanel.add(refreshButton);

    return buttonPanel;
  }

  /**
   * Creates menu at the top.
   *
   * @return the menu
   */
  private JMenuBar createMenuBar() {

    JMenuItem saveItem = new JMenuItem("Save Vault");
    JMenuItem lockItem = new JMenuItem("Lock Vault");
    JMenuItem exitItem = new JMenuItem("Exit");

    saveItem.addActionListener(e -> saveVault());
    lockItem.addActionListener(e -> lockVault());
    exitItem.addActionListener(e -> System.exit(0));

    JMenu fileMenu = new JMenu("File");

    fileMenu.add(saveItem);
    fileMenu.add(lockItem);
    fileMenu.addSeparator();
    fileMenu.add(exitItem);

    JMenu toolsMenu = new JMenu("Tools");
    JMenuItem generateItem = new JMenuItem("Generate Password");

    toolsMenu.add(generateItem);
    toolsMenu.addSeparator();
    JMenuItem importItem = new JMenuItem("Import...");
    JMenuItem exportItem = new JMenuItem("Export...");
    toolsMenu.add(importItem);
    toolsMenu.add(exportItem);

    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About");
    aboutItem.addActionListener(e -> showAboutDialog());

    helpMenu.add(aboutItem);

    JMenuBar menuBar = new JMenuBar();

    menuBar.add(fileMenu);
    menuBar.add(toolsMenu);
    menuBar.add(helpMenu);

    return menuBar;
  }

  // Controls what data appears in the table
  class PasswordTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private final String[] columnNames = { "Website", "Username", "Category", "Password", "Notes" };
    private List<PasswordEntry> entries;

    public PasswordTableModel() {
      entries = vault.getAllEntries();
    }

    public int getRowCount() {
      return entries.size();
    }

    public int getColumnCount() {
      return columnNames.length;
    }

    public String getColumnName(int column) {
      return columnNames[column];
    }

    public Object getValueAt(int row, int column) {
      PasswordEntry entry = entries.get(row);
      switch (column) {
        case 0:
          return entry.getUrl();
        case 1:
          return entry.getUsername();
        case 2:
          return entry.getCategory();
        case 3:
          return "••••••••"; // Masked password
        case 4:
          return entry.getNotes();
        default:
          return null;
      }
    }

    /**
     * Getter for the PasswordEntry at a certain row.
     *
     * @param row selected
     * @return selected row
     */
    public PasswordEntry getEntryAt(int row) {
      return entries.get(row);
    }

    /**
     * Update the entries.
     *
     * @param newEntries added
     */
    public void updateEntries(List<PasswordEntry> newEntries) {
      this.entries = newEntries;
      fireTableDataChanged();
    }
  }

  /**
   * Custom renderer for password column, always shows same number of dots instead
   * of the real
   * password.
   */
  class PasswordCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
      JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected,
          hasFocus, row, column);

      if (column == 3) {
        label.setText("••••••••");
      }

      return label;
    }
  }

  /**
   * Loads all entries from the vault, refreshes the table, updates the window
   * title.
   */
  private void loadPasswordEntries() {
    tableModel.updateEntries(vault.getAllEntries());
    updateTitle();
    updateHeaderLabel();
  }

  /**
   * Filter entries using the vault's search method.
   *
   * @param searchText being searched
   */
  private void filterTable(String searchText) {
    List<PasswordEntry> filtered = vault.search(searchText);
    tableModel.updateEntries(filtered);
  }

  /**
   * Place holder for add entry.
   */
  private void addNewEntry() {
    EntryDialog dialog = new EntryDialog(this, "Add New Password Entry", null);
    dialog.setVisible(true);

    if (dialog.isSaved()) {
      PasswordEntry newEntry = dialog.getPasswordEntry();
      vault.addEntry(newEntry);
      loadPasswordEntries();

      JOptionPane.showMessageDialog(this, "Entry added successfully for: " + newEntry.getUrl(),
          "Success", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Gets selected row, opens edit dialogue, warns if nothing selected.
   */
  private void editSelectedEntry() {
    int selectedRow = passwordTable.getSelectedRow();
    if (selectedRow < 0) {
      JOptionPane.showMessageDialog(this, "Please select an entry to edit", "No Selection",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    // Get selected entry
    PasswordEntry originalEntry = tableModel.getEntryAt(selectedRow);

    EntryDialog dialog = new EntryDialog(this, "Edit Password Entry", originalEntry);
    dialog.setVisible(true);

    if (dialog.isSaved()) {
      PasswordEntry updatedEntry = dialog.getPasswordEntry();

      loadPasswordEntries();

      JOptionPane.showMessageDialog(this,
          "Entry updated successfully for: " + updatedEntry.getUrl(),
          "Success", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Confirms deletion, removes entry from vault, refreshes table.
   */
  private void deleteSelectedEntry() {
    int selectedRow = passwordTable.getSelectedRow();
    if (selectedRow >= 0) {
      PasswordEntry entry = tableModel.getEntryAt(selectedRow);
      int confirm = JOptionPane.showConfirmDialog(this, "Delete entry for " + entry.getUrl() + "?",
          "Confirm Delete", JOptionPane.YES_NO_OPTION);

      if (confirm == JOptionPane.YES_OPTION) {
        vault.removeEntry(entry.getId());
        loadPasswordEntries();
      }
    }
  }

  /**
   * Copies password to system clipboard.
   */
  private void copyPasswordToClipboard() {
    int selectedRow = passwordTable.getSelectedRow();
    if (selectedRow >= 0) {
      PasswordEntry entry = tableModel.getEntryAt(selectedRow);
      StringSelection stringSelection = new StringSelection(new String(entry.getPassword()));
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

      JOptionPane.showMessageDialog(this, "Password copied to clipboard", "Success",
          JOptionPane.INFORMATION_MESSAGE);
    }
  }

  /**
   * Saves vault to the file.
   */
  private void saveVault() {
    try {
      int confirm = JOptionPane.showConfirmDialog(
          this,
          "Save vault to disk?",
          "Save Vault",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE);

      if (confirm == JOptionPane.YES_OPTION) {
        vaultStorage.saveVault(vault, masterPassword);
        Arrays.fill(masterPassword, '\0');

        JOptionPane.showMessageDialog(this,
            "Vault saved successfully to:\n" + vaultStorage.getVaultFilePath(), "Save Complete",
            JOptionPane.INFORMATION_MESSAGE);
      }
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Error saving vault: " + e.getMessage(), "Save Error",
          JOptionPane.ERROR_MESSAGE);
      e.printStackTrace();
    }
  }

  /**
   * Closes main window, returns user to login screen.
   */
  private void lockVault() {
    int confirm = JOptionPane.showConfirmDialog(this, "Lock vault and return to login?",
        "Lock Vault", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
      dispose();
      new LoginScreen();
    }
  }

  /**
   * About dialogue.
   */
  private void showAboutDialog() {
    JOptionPane.showMessageDialog(this, "Password Vault\nVersion 1.0\n\nA secure password manager",
        "About", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Update title of password vault.
   */
  private void updateTitle() {
    setTitle("Password Vault - " + vault.getName() + " (" + vault.getEntryCount() + " entries)");
  }

  /**
   * Dialog for adding and editting password entries.
   */
  class EntryDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JTextField urlField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField categoryField;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean saved = false;
    private PasswordEntry entry;

    public EntryDialog(JFrame parent, String title, PasswordEntry existingEntry) {
      super(parent, title, true);
      this.entry = existingEntry;

      setSize(500, 400);
      setLocationRelativeTo(parent);
      setLayout(new BorderLayout(10, 10));

      // Create main panel with form
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.insets = new Insets(5, 5, 5, 5);
      gbc.fill = GridBagConstraints.HORIZONTAL;

      JPanel formPanel = new JPanel(new GridBagLayout());

      // URL
      gbc.gridx = 0;
      gbc.gridy = 0;
      formPanel.add(new JLabel("Website/URL:"), gbc);

      gbc.gridx = 1;
      gbc.weightx = 1.0;
      urlField = new JTextField(30);
      formPanel.add(urlField, gbc);

      // Username
      gbc.gridx = 0;
      gbc.gridy = 1;
      gbc.weightx = 0;
      formPanel.add(new JLabel("Username:"), gbc);

      gbc.gridx = 1;
      gbc.weightx = 1.0;
      usernameField = new JTextField(30);
      formPanel.add(usernameField, gbc);

      // Password
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.weightx = 0;
      formPanel.add(new JLabel("Password:"), gbc);

      gbc.gridx = 1;
      gbc.weightx = 1.0;
      JPanel passwordPanel = new JPanel(new BorderLayout(5, 0));
      passwordField = new JPasswordField(20);
      passwordPanel.add(passwordField, BorderLayout.CENTER);

      formPanel.add(passwordPanel, gbc);

      // Category
      gbc.gridx = 0;
      gbc.gridy = 3;
      gbc.weightx = 0;
      formPanel.add(new JLabel("Category:"), gbc);

      gbc.gridx = 1;
      gbc.weightx = 1.0;
      categoryField = new JTextField(30);
      formPanel.add(categoryField, gbc);

      // Notes
      gbc.gridx = 0;
      gbc.gridy = 4;
      gbc.weightx = 0;
      formPanel.add(new JLabel("Notes:"), gbc);

      gbc.gridx = 1;
      gbc.gridy = 4;
      gbc.weightx = 1.0;
      gbc.weighty = 1.0;
      gbc.fill = GridBagConstraints.BOTH;
      notesArea = new JTextArea(5, 30);
      notesArea.setLineWrap(true);
      notesArea.setWrapStyleWord(true);
      JScrollPane notesScroll = new JScrollPane(notesArea);
      formPanel.add(notesScroll, gbc);

      // Button panel
      saveButton = new JButton("Save");
      cancelButton = new JButton("Cancel");

      saveButton.addActionListener(e -> {
        if (validateFields()) {
          saved = true;
          dispose();
        }
      });

      cancelButton.addActionListener(e -> dispose());

      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      buttonPanel.add(saveButton);
      buttonPanel.add(cancelButton);

      // Add panels to dialog
      add(formPanel, BorderLayout.CENTER);
      add(buttonPanel, BorderLayout.SOUTH);

      // If editing existing entry, populate fields
      if (existingEntry != null) {
        urlField.setText(existingEntry.getUrl());
        usernameField.setText(existingEntry.getUsername());
        passwordField.setText(new String(existingEntry.getPassword()));
        categoryField.setText(existingEntry.getCategory());
        notesArea.setText(existingEntry.getNotes());
      }

      // Set default button
      getRootPane().setDefaultButton(saveButton);
    }

    /**
     * Validates all fields before saving.
     *
     * @return true if all fields are valid
     */
    private boolean validateFields() {
      if (urlField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Website/URL is required", "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        urlField.requestFocus();
        return false;
      }

      if (usernameField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Username is required", "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        usernameField.requestFocus();
        return false;
      }

      if (passwordField.getPassword().length == 0) {
        JOptionPane.showMessageDialog(this, "Password is required", "Validation Error",
            JOptionPane.ERROR_MESSAGE);
        passwordField.requestFocus();
        return false;
      }

      return true;
    }

    /**
     * Returns whether the dialog was saved or cancelled.
     *
     * @return true if saved, false if cancelled
     */
    public boolean isSaved() {
      return saved;
    }

    /**
     * Gets the password entry from the dialog fields.
     *
     * @return the password entry
     */
    public PasswordEntry getPasswordEntry() {
      if (entry == null) {
        // Create new entry
        entry = new PasswordEntry(urlField.getText().trim(),
            usernameField.getText().trim(), passwordField.getPassword(), urlField.getText().trim(),
            notesArea.getText().trim(), categoryField.getText().trim());
      } else {
        // Update existing entry
        entry.setUrl(urlField.getText().trim());
        entry.setUsername(usernameField.getText().trim());
        entry.setPassword(passwordField.getPassword());
        entry.setCategory(categoryField.getText().trim());
        entry.setNotes(notesArea.getText().trim());
      }
      return entry;
    }
  }

  /**
   * Update the header label, so it displays the right entry count.
   */
  private void updateHeaderLabel() {
    vaultLabel.setText(vault.getName() + " (" + vault.getEntryCount() + " entries)");
  }
}
