package com.izinlapor.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.izinlapor.App;
import com.izinlapor.dao.ActivityLogDAO;
import com.izinlapor.dao.ReportDAO;
import com.izinlapor.dao.UserDAO;
import com.izinlapor.model.ActivityLog;
import com.izinlapor.model.Report;
import com.izinlapor.model.User;
import com.izinlapor.util.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

public class AdminDashboardController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab manageReportsTab;
    @FXML private Tab manageUsersTab;

    @FXML private ImageView sidebarProfileImageView;
    @FXML private Label sidebarNameLabel;

    @FXML private Label totalReportsLabel;
    @FXML private Label processingReportsLabel;
    @FXML private Label finishedReportsLabel;

    // Reports Tab
    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, String> colDate;
    @FXML private TableColumn<Report, String> colTitle;
    @FXML private TableColumn<Report, String> colStatus;
    @FXML private TableColumn<Report, String> colUser;

    @FXML private ComboBox<String> statusComboBox;
    @FXML private ImageView evidenceImageView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea responseArea;

    // Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colUserNik;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colUserUsername;
    @FXML private TableColumn<User, String> colUserRole;
    @FXML private TextField userSearchField;

    @FXML private ImageView userProfileImageView;
    @FXML private Label detailNikLabel;
    @FXML private Label detailNameLabel;
    @FXML private Label detailUsernameLabel;
    @FXML private Label detailPhoneLabel;
    @FXML private Label detailAddressLabel;

    private ReportDAO reportDAO = new ReportDAO();
    private ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        setupTable();
        setupUserTable();
        updateSidebarProfile();
        
        // Initial load
        loadStatistics();
        loadReports();
        
        // Refresh data when tabs change
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == dashboardTab) {
                loadStatistics();
            } else if (newTab == manageReportsTab) {
                loadReports();
            } else if (newTab == manageUsersTab) {
                loadUsers();
            }
        });
        
        filterStatusComboBox.setItems(FXCollections.observableArrayList("SEMUA", "BARU", "DIPROSES", "SELESAI", "DITOLAK"));
        filterStatusComboBox.setValue("SEMUA");
        
        statusComboBox.setItems(FXCollections.observableArrayList("BARU", "DIPROSES", "SELESAI", "DITOLAK"));
    }

    private void updateSidebarProfile() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            sidebarNameLabel.setText(user.getFullName());
            if (user.getPhotoProfile() != null) {
                try {
                    File file = new File(user.getPhotoProfile());
                    if (file.exists()) {
                        sidebarProfileImageView.setImage(new Image(file.toURI().toString()));
                    } else {
                        // Fallback to default if file missing
                        sidebarProfileImageView.setImage(new Image(getClass().getResourceAsStream("/com/izinlapor/images/default_profile.png")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                 // Default image
                 try {
                    sidebarProfileImageView.setImage(new Image(getClass().getResourceAsStream("/com/izinlapor/images/default_profile.png")));
                 } catch (Exception e) {
                     // Ignore if default image not found
                 }
            }
        }
    }

    private void setupUserTable() {
        colUserNik.setCellValueFactory(new PropertyValueFactory<>("nik"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUserUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUserRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                detailNikLabel.setText(newSelection.getNik());
                detailNameLabel.setText(newSelection.getFullName());
                detailUsernameLabel.setText(newSelection.getUsername());
                detailPhoneLabel.setText(newSelection.getPhone());
                detailAddressLabel.setText(newSelection.getAddress());

                if (newSelection.getPhotoProfile() != null) {
                    try {
                        File file = new File(newSelection.getPhotoProfile());
                        if (file.exists()) {
                            userProfileImageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            userProfileImageView.setImage(null);
                        }
                    } catch (Exception e) {
                        userProfileImageView.setImage(null);
                    }
                } else {
                    userProfileImageView.setImage(null);
                }
            } else {
                clearUserDetails();
            }
        });
    }

    private void clearUserDetails() {
        detailNikLabel.setText("-");
        detailNameLabel.setText("-");
        detailUsernameLabel.setText("-");
        detailPhoneLabel.setText("-");
        detailAddressLabel.setText("-");
        userProfileImageView.setImage(null);
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.getAllUsers();
            ObservableList<User> data = FXCollections.observableArrayList(users);
            
            FilteredList<User> filteredData = new FilteredList<>(data, p -> true);
            
            userSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(user -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    
                    String lowerCaseFilter = newValue.toLowerCase();
                    
                    if (user.getFullName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (user.getNik().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            
            SortedList<User> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(usersTable.comparatorProperty());
            
            usersTable.setItems(sortedData);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Gagal memuat data user.");
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih user yang ingin dihapus.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus User: " + selectedUser.getUsername() + "?");
        alert.setContentText("Tindakan ini tidak dapat dibatalkan.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (userDAO.deleteUser(selectedUser.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "User berhasil dihapus.");
                    loadUsers();
                    clearUserDetails();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal menghapus user.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Terjadi kesalahan database.");
            }
        }
    }

    @FXML
    private void handleGoToManageUsers() {
        mainTabPane.getSelectionModel().select(manageUsersTab);
    }

    private void setupTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        reportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                statusComboBox.setValue(newSelection.getStatus());
                responseArea.setText(newSelection.getResponse()); // Load existing response
                if (newSelection.getPhotoPath() != null) {
                    try {
                        File file = new File(newSelection.getPhotoPath());
                        if (file.exists()) {
                            evidenceImageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            evidenceImageView.setImage(null);
                        }
                    } catch (Exception e) {
                        evidenceImageView.setImage(null);
                    }
                } else {
                    evidenceImageView.setImage(null);
                }
            } else {
                evidenceImageView.setImage(null);
                responseArea.clear();
            }
        });
    }

    private void loadStatistics() {
        try {
            int total = reportDAO.countAllReports();
            int processing = reportDAO.countReportsByStatus("DIPROSES");
            int finished = reportDAO.countReportsByStatus("SELESAI");
            
            System.out.println("Loading Stats: Total=" + total + ", Processing=" + processing + ", Finished=" + finished);
            
            totalReportsLabel.setText(String.valueOf(total));
            processingReportsLabel.setText(String.valueOf(processing));
            finishedReportsLabel.setText(String.valueOf(finished));
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }

    private void loadReports() {
        try {
            List<Report> reports = reportDAO.getAllReports();
            ObservableList<Report> data = FXCollections.observableArrayList(reports);
            
            FilteredList<Report> filteredData = new FilteredList<>(data, p -> true);
            
            // Listeners for all filters
            searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter(filteredData));
            filterStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter(filteredData));
            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter(filteredData));
            endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> updateFilter(filteredData));
            
            SortedList<Report> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(reportsTable.comparatorProperty());
            
            reportsTable.setItems(sortedData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFilter(FilteredList<Report> filteredData) {
        filteredData.setPredicate(report -> {
            // 1. Status Filter
            String statusFilter = filterStatusComboBox.getValue();
            if (statusFilter != null && !statusFilter.equals("SEMUA") && !report.getStatus().equalsIgnoreCase(statusFilter)) {
                return false;
            }
            
            // 2. Date Range Filter
            if (startDatePicker.getValue() != null) {
                if (report.getCreatedAt().toLocalDateTime().toLocalDate().isBefore(startDatePicker.getValue())) {
                    return false;
                }
            }
            if (endDatePicker.getValue() != null) {
                if (report.getCreatedAt().toLocalDateTime().toLocalDate().isAfter(endDatePicker.getValue())) {
                    return false;
                }
            }
            
            // 3. Search Text
            String searchText = searchField.getText();
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = searchText.toLowerCase();
            if (report.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            } else if (String.valueOf(report.getUserId()).contains(lowerCaseFilter)) {
                return true;
            }
            
            return false;
        });
    }

    @FXML
    private void handleUpdateStatus() {
        Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        String newStatus = statusComboBox.getValue();
        String response = responseArea.getText();

        if (selectedReport == null || newStatus == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Pilih laporan dan status baru.");
            return;
        }

        try {
            if (reportDAO.updateStatus(selectedReport.getId(), newStatus, response)) {
                // Log activity
                ActivityLog log = new ActivityLog();
                log.setUserId(SessionManager.getCurrentUser().getId());
                log.setReportId(selectedReport.getId());
                log.setAction("Mengubah status laporan #" + selectedReport.getId() + " menjadi " + newStatus);
                activityLogDAO.logActivity(log);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Status dan tanggapan berhasil diperbarui.");
                loadReports();
                loadStatistics();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal memperbarui status.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
        }
    }

    @FXML
    private void handleGoToDashboard() {
        mainTabPane.getSelectionModel().select(dashboardTab);
    }

    @FXML
    private void handleGoToManageReports() {
        mainTabPane.getSelectionModel().select(manageReportsTab);
    }

    @FXML
    private void handleExportReports() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan ke CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("laporan_pengaduan.csv");
        
        File file = fileChooser.showSaveDialog(mainTabPane.getScene().getWindow());
        
        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write Header
                writer.write("ID,User ID,Judul,Isi Laporan,Status,Tanggal Dibuat,Tanggal Update");
                writer.newLine();
                
                // Write Data
                // Use the items currently in the table (respecting filters)
                for (Report report : reportsTable.getItems()) {
                    String line = String.format("%d,%d,\"%s\",\"%s\",%s,%s,%s",
                        report.getId(),
                        report.getUserId(),
                        escapeCsv(report.getTitle()),
                        escapeCsv(report.getContent()),
                        report.getStatus(),
                        report.getCreatedAt(),
                        report.getUpdatedAt()
                    );
                    writer.write(line);
                    writer.newLine();
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Data berhasil diekspor ke " + file.getName());
                
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Gagal menyimpan file: " + e.getMessage());
            }
        }
    }
    
    private String escapeCsv(String text) {
        if (text == null) return "";
        return text.replace("\"", "\"\"");
    }

    @FXML
    private void handleLogout() throws IOException {
        SessionManager.logout();
        App.setRoot("login");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
