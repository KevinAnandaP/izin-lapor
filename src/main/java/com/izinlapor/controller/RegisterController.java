package com.izinlapor.controller;

import com.izinlapor.App;
import com.izinlapor.dao.UserDAO;
import com.izinlapor.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField nikField;
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        if (nikField.getText().isEmpty() || fullNameField.getText().isEmpty() || 
            usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        User user = new User();
        user.setNik(nikField.getText());
        user.setFullName(fullNameField.getText());
        user.setUsername(usernameField.getText());
        user.setPassword(passwordField.getText());
        user.setRole("WARGA");
        user.setPhone(phoneField.getText());
        user.setAddress(addressField.getText());

        try {
            if (userDAO.register(user)) {
                showAlert("Success", "Registration successful! Please login.");
                App.setRoot("login");
            } else {
                showAlert("Error", "Registration failed.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
