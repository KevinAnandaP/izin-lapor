package com.izinlapor.controller;

import com.izinlapor.App;
import com.izinlapor.dao.ReportDAO;
import com.izinlapor.dao.ActivityLogDAO;
import com.izinlapor.model.ActivityLog;
import com.izinlapor.model.Report;
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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboardController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab dashboardTab;
    @FXML private Tab manageReportsTab;

    @FXML private Label totalReportsLabel;
    @FXML private Label processingReportsLabel;
    @FXML private Label finishedReportsLabel;

    @FXML private TableView<Report> reportsTable;
    @FXML private TableColumn<Report, String> colDate;
    @FXML private TableColumn<Report, String> colTitle;
    @FXML private TableColumn<Report, String> colStatus;
    @FXML private TableColumn<Report, String> colUser;

    @FXML private ComboBox<String> statusComboBox;
    @FXML private ImageView evidenceImageView;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterStatusComboBox;

    private ReportDAO reportDAO = new ReportDAO();
    private ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    @FXML
    public void initialize() {
        setupTable();
        loadStatistics();
        
        filterStatusComboBox.setItems(FXCollections.observableArrayList("SEMUA", "BARU", "DIPROSES", "SELESAI", "DITOLAK"));
        filterStatusComboBox.setValue("SEMUA");
        
        loadReports();
        statusComboBox.setItems(FXCollections.observableArrayList("BARU", "DIPROSES", "SELESAI", "DITOLAK"));
    }

    private void setupTable() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("userId"));
        
        reportsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                statusComboBox.setValue(newSelection.getStatus());
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
            }
        });
    }

    private void loadStatistics() {
        try {
            totalReportsLabel.setText(String.valueOf(reportDAO.countAllReports()));
            processingReportsLabel.setText(String.valueOf(reportDAO.countReportsByStatus("DIPROSES")));
            finishedReportsLabel.setText(String.valueOf(reportDAO.countReportsByStatus("SELESAI")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReports() {
        try {
            List<Report> reports = reportDAO.getAllReports();
            ObservableList<Report> data = FXCollections.observableArrayList(reports);
            
            FilteredList<Report> filteredData = new FilteredList<>(data, p -> true);
            
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(report -> isMatch(report, newValue, filterStatusComboBox.getValue()));
            });
            
            filterStatusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(report -> isMatch(report, searchField.getText(), newValue));
            });
            
            SortedList<Report> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(reportsTable.comparatorProperty());
            
            reportsTable.setItems(sortedData);
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
        } else if (String.valueOf(report.getUserId()).contains(lowerCaseFilter)) {
            return true;
        }
        return false;
    }

    @FXML
    private void handleUpdateStatus() {
        Report selectedReport = reportsTable.getSelectionModel().getSelectedItem();
        String newStatus = statusComboBox.getValue();

        if (selectedReport == null || newStatus == null) {
            showAlert("Error", "Pilih laporan dan status baru.");
            return;
        }

        try {
            if (reportDAO.updateStatus(selectedReport.getId(), newStatus)) {
                // Log activity
                ActivityLog log = new ActivityLog();
                log.setUserId(SessionManager.getCurrentUser().getId());
                log.setReportId(selectedReport.getId());
                log.setAction("Mengubah status laporan #" + selectedReport.getId() + " menjadi " + newStatus);
                activityLogDAO.logActivity(log);

                showAlert("Success", "Status berhasil diperbarui.");
                loadReports();
                loadStatistics();
            } else {
                showAlert("Error", "Gagal memperbarui status.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error: " + e.getMessage());
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
    private void handleLogout() throws IOException {
        SessionManager.logout();
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
