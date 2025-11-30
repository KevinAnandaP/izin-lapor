package com.izinlapor.controller;

import com.izinlapor.App;
import com.izinlapor.dao.UserDAO;
import com.izinlapor.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField nikField;
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private ImageView profileImageView;

    private UserDAO userDAO = new UserDAO();
    private File selectedPhoto;

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedPhoto = fileChooser.showOpenDialog(null);
        if (selectedPhoto != null) {
            profileImageView.setImage(new Image(selectedPhoto.toURI().toString()));
            
            // Circular clip
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(
                profileImageView.getFitWidth() / 2, 
                profileImageView.getFitHeight() / 2, 
                profileImageView.getFitWidth() / 2
            );
            profileImageView.setClip(clip);
        }
    }

    @FXML
    private void handleRegister() {
        String nik = nikField.getText().trim();
        
        if (nikField.getText().isEmpty() || fullNameField.getText().isEmpty() || 
            usernameField.getText().isEmpty() || passwordField.getText().isEmpty() ||
            phoneField.getText().isEmpty() || addressField.getText().isEmpty() ||
            emailField.getText().isEmpty()) {
            showAlert("Error", "Harap isi semua field (NIK, Nama, Username, Email, Password, Telepon, Alamat).");
            return;
        }

        User user = new User();
        user.setNik(nik);
        user.setFullName(fullNameField.getText());
        user.setUsername(usernameField.getText());
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        user.setRole("WARGA");
        user.setPhone(phoneField.getText());
        user.setAddress(addressField.getText());
        
        if (selectedPhoto != null) {
            user.setPhotoProfile(selectedPhoto.getAbsolutePath());
        }

        try {
            if (userDAO.register(user)) {
                showAlert("Sukses", "Registrasi berhasil! Silakan login.");
                App.setRoot("login");
            } else {
                showAlert("Error", "Registrasi gagal.");
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
