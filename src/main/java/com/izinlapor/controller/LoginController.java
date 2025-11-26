package com.izinlapor.controller;

import com.izinlapor.App;
import com.izinlapor.dao.UserDAO;
import com.izinlapor.model.User;
import com.izinlapor.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and Password cannot be empty.");
            return;
        }

        try {
            User user = userDAO.login(username, password);
            if (user != null) {
                SessionManager.setCurrentUser(user);
                if ("ADMIN".equals(user.getRole())) {
                    App.setRoot("admin_dashboard");
                } else {
                    App.setRoot("citizen_dashboard");
                }
            } else {
                showAlert("Error", "Invalid username or password.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void switchToRegister() throws IOException {
        App.setRoot("register");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
