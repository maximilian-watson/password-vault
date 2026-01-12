# Password Vault

A desktop password vault application written in Java using Swing for the user interface and AES-GCM encryption for local storage.

# Features

## Secure Vault

- Vault is encrypted using AES-GCM with a master password
- Random salt used for key derivation
- Vault data is stored encrypted on disk
- Vault is only unlocked with the correct master password, chosen when creating a new vault

## Desktop GUI

- Using swing
- Login screen for unlocking or creating a vault
- Modal dialogs for adding and editing password entries
- Keyboard friendly UI, double click an entry to edit, enter key to submit

## Password Management

### Store

- Website / URL
- Username
- Password
- Category
- Notes

### Add, edit and delete entries

### Search entries by

- URL
- Username
- Category
- Notes

### Data safety practices

- Passwords handled using char[] instead of string
- Sensitive data cleared, after use
- Validation on user inputs

## This project used

- Java
- Swing (JFrame, JDialog, JTable, layouts)
- AES-GCM encryption
- Gson for JSON serialisation
- File I/O (NIO)
- Object Orientatated Design

### Build and Quality Tools

- Maven for dependency management and builds
- Checkstyle for consistent code formatting and style
- SpotBugs for static code analysis
- JaCoCo for test coverage reporting
- GitHub Actions for basic CIi

## This project demonstrates

- Building a desktop GUI using Swing, with mutliple windows
- Designing modal dialogs with form validation
- Applying real cryptographic conecepts (AES-GCM, salts, key derivation)
- Separation of concerns, GUI, storage, encryption, models
- Safely handle sensitive data in java
- Implement searching and filtering logic
- Readable, documentable, maintainable code, regular commits

# How to use

1. Run the main method in LoginScreen.
1. Build and run the application
   #### Using Maven (recommended)

```bash
mvn clean package
```

then run

```bash
java -cp target/password-vault.jar com.mwatson.passwordvault.gui.LoginScreen
```

1. Directly run from an IDE

2. Create a new vault

- Enter a new master password
- Click create new vault
- Vault is encrypted and stored locally

3. Unlock a vault

- Enter master password
- Click unlock vault
- If the password is correct, the vault opens

4. Manage Passwords

- Add new password entries
- Edit or delete existing entries

5. Delete vault

- Permanently deletes the encrypted vault file
- All stored data is deleted

# Design Decisions

This project intentially avoids Java's built in object serialisation for storing data, as I encoutered problems storing certain types and struggled to use.

Vault data is manually serialised to JSON and encrypted before being saved to disk, giving full control over storage format.

Using JSON and encryption gave me more control over the format and security.

```

```
