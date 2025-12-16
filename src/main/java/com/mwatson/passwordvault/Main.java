package com.mwatson.passwordvault;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main entry point for the Password Vault application. 
 * Initialises the JavaFX application window.
 */
public class Main extends Application {

  @Override
  public void start(Stage primaryStage) {
    Label label = new Label("Password Vault - Coming Soon!!");
    StackPane root = new StackPane(label);
    Scene scene = new Scene(root, 400, 300);

    primaryStage.setTitle("Password Vault");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Main method to launch the JavaFX application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
