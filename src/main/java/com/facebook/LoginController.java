package com.facebook;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        // Logic from Main.java
        if (Database.Check_Database()) { // Ensure DB is ready
             User user = Database.LoadUser(username);
             if (user != null) {
                 if (user.getCredentials().p_Verify(password)) {
                     errorLabel.setText("Login Successful!");
                     // TODO: Navigate to Home Page
                     // For now just show success
                     Main.current = user; // Set current user
                     Database.Write_Online(); // Mark online
                     System.out.println("User logged in: " + username);
                 } else {
                     errorLabel.setText("Invalid Password!");
                 }
             } else {
                 errorLabel.setText("User not found!");
             }
        } else {
            errorLabel.setText("Database error.");
        }
    }

    @FXML
    protected void onSignupButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Facebook Sign Up");
        stage.setScene(scene);
        stage.show();
    }
}
