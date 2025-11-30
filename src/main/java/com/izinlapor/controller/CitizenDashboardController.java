package com.izinlapor.controller;

import com.izinlapor.App;
import com.izinlapor.dao.CommentDAO;
import com.izinlapor.dao.ReportDAO;
import com.izinlapor.dao.UserDAO;
import com.izinlapor.model.Comment;
import com.izinlapor.model.Report;
import com.izinlapor.model.User;
import com.izinlapor.util.FileUtil;
import com.izinlapor.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CitizenDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label recentStatusLabel;
    
    @FXML private ImageView sidebarProfileImageView;
    @FXML private Label sidebarNameLabel;
    
    @FXML private TabPane mainTabPane;
    @FXML private Tab homeTab;
    @FXML private Tab createReportTab;
    @FXML private Tab historyTab;
    @FXML private Tab profileTab;

    // Create Report Tab
    @FXML private TextField titleField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TextArea contentField;
    @FXML private Label photoPathLabel;
    @FXML private ImageView previewImageView;
    private File selectedPhoto;

    // History Tab
    @FXML private TableView<Report> historyTable;
    @FXML private TableColumn<Report, String> colDate;
    @FXML private TableColumn<Report, String> colTitle;
    @FXML private TableColumn<Report, String> colCategory;
    @FXML private TableColumn<Report, String> colStatus;
    @FXML private ImageView historyImageView;
    @FXML private TextArea responseArea;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusComboBox;
    @FXML private Button deleteReportButton;
    
    @FXML private ListView<String> chatListView;
    @FXML private TextField chatInputField;

    // Edit Profile Tab
    @FXML private TextField editNikField;
    @FXML private TextField editUsernameField;
    @FXML private TextField editEmailField;
    @FXML private TextField editNameField;
    @FXML private TextField editPhoneField;
    @FXML private TextArea editAddressField;
    
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ImageView profileImageView;
    private File selectedProfilePhoto;

    private ReportDAO reportDAO = new ReportDAO();
    private UserDAO userDAO = new UserDAO();
    private CommentDAO commentDAO = new CommentDAO();

    @FXML
    public void initialize() {
        updateWelcomeLabel();
        updateSidebarProfile();
        loadRecentStatus();
        setupHistoryTable();
        
        categoryComboBox.setItems(FXCollections.observableArrayList("Infrastruktur", "Kebersihan", "Keamanan", "Administrasi", "Lainnya"));
        
        filterStatusComboBox.setItems(FXCollections.observableArrayList("SEMUA", "BARU", "DIPROSES", "SELESAI", "DITOLAK"));
        filterStatusComboBox.setValue("SEMUA");
        
        loadHistory();
        loadProfileData();
    }

    private void updateWelcomeLabel() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            welcomeLabel.setText(user.getFullName() + "'s Dashboard");
        }
    }

    private void updateSidebarProfile() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            sidebarNameLabel.setText(user.getFullName());
            if (user.getPhotoProfile() != null) {
                try {
                    String fullPath = FileUtil.getFullPath(user.getPhotoProfile());
                    File file = new File(fullPath);
                    if (file.exists()) {
                        sidebarProfileImageView.setImage(new Image(file.toURI().toString()));
                        
                        // Optional: Make it circular clip
                        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(
                            sidebarProfileImageView.getFitWidth() / 2, 
                            sidebarProfileImageView.getFitHeight() / 2, 
                            sidebarProfileImageView.getFitWidth() / 2
                        );
                        sidebarProfileImageView.setClip(clip);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfileData() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            editNikField.setText(user.getNik());
            editUsernameField.setText(user.getUsername());
            editEmailField.setText(user.getEmail());
            editNameField.setText(user.getFullName());
            editPhoneField.setText(user.getPhone());
            editAddressField.setText(user.getAddress());
            
            if (user.getPhotoProfile() != null) {
                try {
                    String fullPath = FileUtil.getFullPath(user.getPhotoProfile());
                    File file = new File(fullPath);
                    if (file.exists()) {
                        profileImageView.setImage(new Image(file.toURI().toString()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleChooseProfilePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedProfilePhoto = fileChooser.showOpenDialog(null);
        if (selectedProfilePhoto != null) {
            profileImageView.setImage(new Image(selectedProfilePhoto.toURI().toString()));
        }
    }

    @FXML
    private void handleUpdateProfile() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String newName = editNameField.getText();
            String newEmail = editEmailField.getText();
            String newPhone = editPhoneField.getText();
            String newAddress = editAddressField.getText();

            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
                showAlert("Error", "Semua field harus diisi!");
                return;
            }

            user.setFullName(newName);
            user.setEmail(newEmail);
            user.setPhone(newPhone);
            user.setAddress(newAddress);
            
            if (selectedProfilePhoto != null) {
                try {
                    // Save to project folder
                    String savedPath = FileUtil.saveImage(selectedProfilePhoto, "profiles");
                    user.setPhotoProfile(savedPath);
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert("Error", "Gagal menyimpan foto profil: " + e.getMessage());
                    return;
                }
            }

            try {
                if (userDAO.updateUser(user)) {
                    SessionManager.setCurrentUser(user); // Update session
                    updateWelcomeLabel(); // Refresh label
                    updateSidebarProfile(); // Refresh sidebar
                    showAlert("Sukses", "Profil berhasil diperbarui!");
                } else {
                    showAlert("Gagal", "Gagal memperbarui profil.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Terjadi kesalahan database: " + e.getMessage());
            }
        }
    }

    private void loadRecentStatus() {
        try {
            List<Report> reports = reportDAO.getReportsByUserId(SessionManager.getCurrentUser().getId());
            if (!reports.isEmpty()) {
                Report latest = reports.get(0);
                recentStatusLabel.setText("Laporan terakhir Anda: " + latest.getTitle() + " [" + latest.getStatus() + "]");
            } else {
                recentStatusLabel.setText("Belum ada laporan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupHistoryTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        historyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                responseArea.setText(newSelection.getResponse());
                
                // Enable delete button only if status is BARU
                deleteReportButton.setDisable(!"BARU".equalsIgnoreCase(newSelection.getStatus()));
                
                loadComments(newSelection.getId());
                
                if (newSelection.getPhotoPath() != null) {
                    try {
                        String fullPath = FileUtil.getFullPath(newSelection.getPhotoPath());
                        File file = new File(fullPath);
                        if (file.exists()) {
                            historyImageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            historyImageView.setImage(null);
                        }
                    } catch (Exception e) {
                        historyImageView.setImage(null);
                    }
                } else {
                    historyImageView.setImage(null);
                }
            } else {
                historyImageView.setImage(null);
                responseArea.clear();
                deleteReportButton.setDisable(true);
                chatListView.getItems().clear();
            }
        });
    }

    private void loadComments(int reportId) {
        try {
            List<Comment> comments = commentDAO.getCommentsByReportId(reportId);
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Comment c : comments) {
                items.add(c.toString());
            }
            chatListView.setItems(items);
            chatListView.scrollTo(items.size() - 1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSendChat() {
        Report selectedReport = historyTable.getSelectionModel().getSelectedItem();
        String message = chatInputField.getText();
        
        if (selectedReport == null || message.isEmpty()) {
            return;
        }
        
        Comment comment = new Comment();
        comment.setReportId(selectedReport.getId());
        comment.setUserId(SessionManager.getCurrentUser().getId());
        comment.setMessage(message);
        
        try {
            if (commentDAO.addComment(comment)) {
                chatInputField.clear();
                loadComments(selectedReport.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal mengirim pesan: " + e.getMessage());
        }
    }

    private void loadHistory() {
        try {
            List<Report> reports = reportDAO.getReportsByUserId(SessionManager.getCurrentUser().getId());
            ObservableList<Report> data = FXCollections.observableArrayList(reports);
            
            FilteredList<Report> filteredData = new FilteredList<>(data, p -> true);
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(report -> isMatch(report, newValue, filterStatusComboBox.getValue()));
            });
            
            filterStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(report -> isMatch(report, searchField.getText(), newValue));
            });
            
            SortedList<Report> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(historyTable.comparatorProperty());
            
            historyTable.setItems(sortedData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isMatch(Report report, String searchText, String statusFilter) {
        if (statusFilter != null && !statusFilter.equals("SEMUA") && !report.getStatus().equalsIgnoreCase(statusFilter)) {
            return false;
        }
        
        if (searchText == null || searchText.isEmpty()) {
            return true;
        }
        
        String lowerCaseFilter = searchText.toLowerCase();
        
        if (report.getTitle().toLowerCase().contains(lowerCaseFilter)) {
            return true;
        }
        return false;
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Bukti");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        selectedPhoto = fileChooser.showOpenDialog(null);
        if (selectedPhoto != null) {
            photoPathLabel.setText(selectedPhoto.getAbsolutePath());
            previewImageView.setImage(new Image(selectedPhoto.toURI().toString()));
        }
    }

    @FXML
    private void handleSubmitReport() {
        if (titleField.getText().isEmpty() || contentField.getText().isEmpty() || 
            categoryComboBox.getValue() == null || selectedPhoto == null) {
            showAlert("Error", "Semua field wajib diisi (Judul, Kategori, Isi, Foto Bukti).");
            return;
        }

        Report report = new Report();
        report.setUserId(SessionManager.getCurrentUser().getId());
        report.setTitle(titleField.getText());
        report.setCategory(categoryComboBox.getValue() != null ? categoryComboBox.getValue() : "Lainnya");
        report.setContent(contentField.getText());
        
        if (selectedPhoto != null) {
            try {
                // Save to project folder
                String savedPath = FileUtil.saveImage(selectedPhoto, "reports");
                report.setPhotoPath(savedPath);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Gagal menyimpan foto bukti: " + e.getMessage());
                return;
            }
        }

        try {
            if (reportDAO.createReport(report)) {
                showAlert("Success", "Laporan berhasil dikirim.");
                titleField.clear();
                contentField.clear();
                categoryComboBox.setValue(null);
                photoPathLabel.setText("");
                previewImageView.setImage(null);
                selectedPhoto = null;
                loadRecentStatus();
                loadHistory();
            } else {
                showAlert("Error", "Gagal mengirim laporan.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteReport() {
        Report selectedReport = historyTable.getSelectionModel().getSelectedItem();
        if (selectedReport == null) {
            showAlert("Error", "Pilih laporan yang ingin dihapus.");
            return;
        }

        if (!"BARU".equalsIgnoreCase(selectedReport.getStatus())) {
            showAlert("Error", "Hanya laporan dengan status BARU yang dapat dihapus.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Laporan: " + selectedReport.getTitle() + "?");
        alert.setContentText("Tindakan ini tidak dapat dibatalkan.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                if (reportDAO.deleteReport(selectedReport.getId())) {
                    showAlert("Sukses", "Laporan berhasil dihapus.");
                    loadHistory();
                    loadRecentStatus();
                    historyTable.getSelectionModel().clearSelection();
                } else {
                    showAlert("Error", "Gagal menghapus laporan.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Database error: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleGoToHome() {
        mainTabPane.getSelectionModel().select(homeTab);
    }

    @FXML
    private void handleGoToCreateReport() {
        mainTabPane.getSelectionModel().select(createReportTab);
    }

    @FXML
    private void handleGoToHistory() {
        mainTabPane.getSelectionModel().select(historyTab);
    }

    @FXML
    private void handleGoToProfile() {
        mainTabPane.getSelectionModel().select(profileTab);
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionManager.logout();
        App.setRoot("login");
    }

    @FXML
    private void handleChangePassword() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String oldPass = oldPasswordField.getText();
            String newPass = newPasswordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showAlert("Error", "Semua field password harus diisi!");
                return;
            }

            if (!oldPass.equals(user.getPassword())) {
                showAlert("Error", "Password lama salah!");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                showAlert("Error", "Konfirmasi password baru tidak cocok!");
                return;
            }

            try {
                if (userDAO.updatePassword(user.getId(), newPass)) {
                    user.setPassword(newPass); // Update session
                    SessionManager.setCurrentUser(user);
                    oldPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();
                    showAlert("Sukses", "Password berhasil diubah!");
                } else {
                    showAlert("Gagal", "Gagal mengubah password.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Terjadi kesalahan database: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
