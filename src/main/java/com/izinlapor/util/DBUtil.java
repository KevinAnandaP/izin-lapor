package com.izinlapor.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
    private static final String BASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "izin-lapor";
    private static final String URL = BASE_URL + DB_NAME;
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Default XAMPP password is empty

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(BASE_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            // Create Database
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DB_NAME + "`");
            
            // Use Database
            stmt.executeUpdate("USE `" + DB_NAME + "`");
            
            // Create Users Table
            String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "nik VARCHAR(20) UNIQUE NOT NULL," +
                    "full_name VARCHAR(100) NOT NULL," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "role ENUM('WARGA', 'ADMIN') NOT NULL DEFAULT 'WARGA'," +
                    "phone VARCHAR(20)," +
                    "address TEXT," +
                    "photo_profile VARCHAR(255)," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createUsers);
            
            // Attempt to add photo_profile column if it doesn't exist (for existing databases)
            try {
                stmt.executeUpdate("ALTER TABLE users ADD COLUMN photo_profile VARCHAR(255)");
            } catch (SQLException e) {
                // Column likely already exists, ignore
            }

            // Create Reports Table
            String createReports = "CREATE TABLE IF NOT EXISTS reports (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "title VARCHAR(100) NOT NULL," +
                    "content TEXT NOT NULL," +
                    "photo_path VARCHAR(255)," +
                    "status ENUM('BARU', 'DIPROSES', 'SELESAI', 'DITOLAK') NOT NULL DEFAULT 'BARU'," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")";
            stmt.executeUpdate(createReports);

            // Create Activity Logs Table
            String createLogs = "CREATE TABLE IF NOT EXISTS activity_logs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "report_id INT," +
                    "action VARCHAR(255) NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)," +
                    "FOREIGN KEY (report_id) REFERENCES reports(id)" +
                    ")";
            stmt.executeUpdate(createLogs);

            // Insert Default Admin if not exists
            String insertAdmin = "INSERT IGNORE INTO users (nik, full_name, username, password, role) VALUES " +
                    "('0000000000000000', 'Administrator', 'admin', 'admin123', 'ADMIN')";
            stmt.executeUpdate(insertAdmin);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
