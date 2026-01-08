package com.mwatson.passwordvault.gui;

import com.mwatson.passwordvault.model.Vault;
import com.mwatson.passwordvault.storage.VaultStorage;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Login screen for the Password Vault application.
 *
 * <p>This screen allows the user to unlock an existing vault
 * or create a new vault using a master password.
 */
public class LoginScreen extends JFrame {
  private static final long serialVersionUID = 1L;
  private final VaultStorage storage;
  private JPasswordField passwordField;

  /**
   * Login screen constructor, initialises the user interface.
   */
  public LoginScreen() {
    this.storage = new VaultStorage(); // Default path in home directory
    setUpUi();
    setVisible(true);
  }

  private void setUpUi() {
    // Title and basic frame configuration
    setTitle("Password Vault - Login");
    setSize(400, 250);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Centre the window on the screen
    setLocationRelativeTo(null);
    setResizable(false);

    // Main container panel using BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header label displayed at the top of the window
    JLabel headerLabel = new JLabel("Password Vault", SwingConstants.CENTER);
    headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
    mainPanel.add(headerLabel, BorderLayout.NORTH);

    // Panel containing the master password label and input field
    JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    passwordPanel.add(new JLabel("Master Password:"));
    passwordField = new JPasswordField(20);
    passwordPanel.add(passwordField);

    // Centre form panel holding password field, buttons and status text
    JPanel formPanel = new JPanel(new GridLayout(3, 1, 10, 10));
    formPanel.add(passwordPanel);

    // Panel containing action buttons
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

    // Button used to unlock an existing vault
    JButton unlockButton = new JButton("Unlock Vault");
    unlockButton.addActionListener(e -> unlockVault());

    // Button used to create a new vault
    JButton createButton = new JButton("Create New Vault");
    createButton.addActionListener(e -> createNewVault());

    buttonPanel.add(unlockButton);
    buttonPanel.add(createButton);
    formPanel.add(buttonPanel);

    // Label for displaying status or error messages
    JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    statusLabel.setForeground(Color.RED);
    formPanel.add(statusLabel);

    // Add the form panel to the centre of the main layout
    mainPanel.add(formPanel, BorderLayout.CENTER);

    // Allow pressing enter to trigger unlock button
    getRootPane().setDefaultButton(unlockButton);

    // Add the main panel to the frame
    add(mainPanel);
  }

  private void unlockVault() {
    // Retrieve the master password entered by the user
    char[] password = passwordField.getPassword();

    // Ensure a password was entered
    if (password.length == 0) {
      JOptionPane.showMessageDialog(this, "Please enter a password", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    try {
      // Attempt to load the vault using the provided password
      Vault vault = storage.loadVault(password);

      // If no vault is found, prompt to create a new vault
      if (vault == null) {
        // No vault exists
        int choice = JOptionPane.showConfirmDialog(this, "No vault found. Create a new one?",
            "Vault Not Found", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
          createNewVaultWithPassword(password);
        }
        return;
      }

      // Vault successfully unlocked, open the main application window
      openMainWindow(vault, password);

    } catch (Exception e) {
      // Display an error message if unlocking fails
      JOptionPane.showMessageDialog(this, "Failed to unlock vault: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void createNewVault() {
    // Read the password entered by the user
    char[] password = passwordField.getPassword();

    // Don't allow an empty password
    if (password.length == 0) {
      JOptionPane.showMessageDialog(this, "Please enter a password for the new vault", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Create the vault using the provided password
    createNewVaultWithPassword(password);
  }

  private void createNewVaultWithPassword(char[] password) {
    try {
      // Create a new empty vault
      Vault newVault = new Vault();

      // Save the vault to disk, encrypted with the master password
      storage.saveVault(newVault, password);

      // Tell the user that the vault was created successfully
      JOptionPane.showMessageDialog(this, "New vault created successfully!", "Success",
          JOptionPane.INFORMATION_MESSAGE);

      // Open main application window
      openMainWindow(newVault, password);

    } catch (Exception e) {
      // Show an error if vault creation or saving fails
      JOptionPane.showMessageDialog(this, "Failed to create vault: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private void openMainWindow(Vault vault, char[] password) {
    // TODO: Create and show main window
    // Temporary console output for debugging
    System.out.println("Vault unlocked! Name: " + vault.getName());
    System.out.println("Entries: " + vault.getEntryCount());

    // For now, shows simple success message to user
    JOptionPane.showMessageDialog(this, "Vault unlocked successfully!\n" + "Name: "
        + vault.getName() + "\n" + "Entries: " + vault.getEntryCount(), "Success",
        JOptionPane.INFORMATION_MESSAGE);

    // Clear password from memory
    Arrays.fill(password, '\0');

    // TODO: Remove this later - just for testing
    // Close the login window, main window will replace later
    dispose();
  }

  /**
   * Application entry point.
   *
   * @param args command line not used
   */
  public static void main(String[] args) {
    // Use SwingUtilities to ensure thread safety
    SwingUtilities.invokeLater(() -> {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
        // Use default look and feel
      }

      new LoginScreen();
    });
  }
}