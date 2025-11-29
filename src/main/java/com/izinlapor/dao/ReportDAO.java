package com.izinlapor.dao;

import com.izinlapor.model.Report;
import com.izinlapor.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public boolean createReport(Report report) throws SQLException {
        String sql = "INSERT INTO reports (user_id, title, category, content, photo_path, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, report.getUserId());
            pstmt.setString(2, report.getTitle());
            pstmt.setString(3, report.getCategory());
            pstmt.setString(4, report.getContent());
            pstmt.setString(5, report.getPhotoPath());
            pstmt.setString(6, "BARU");
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteReport(int reportId) throws SQLException {
        String sql = "DELETE FROM reports WHERE id = ? AND status = 'BARU'";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Report> getReportsByUserId(int userId) throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapResultSetToReport(rs));
                }
            }
        }
        return reports;
    }

    public List<Report> getAllReports() throws SQLException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports ORDER BY created_at DESC";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
        }
        return reports;
    }

    public boolean updateStatus(int reportId, String status, String response) throws SQLException {
        String sql = "UPDATE reports SET status = ?, response = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, response);
            pstmt.setInt(3, reportId);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        return new Report(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getString("title"),
            rs.getString("category"),
            rs.getString("content"),
            rs.getString("photo_path"),
            rs.getString("status"),
            rs.getString("response"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
    
    public int countReportsByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public int countAllReports() throws SQLException {
        String sql = "SELECT COUNT(*) FROM reports";
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
