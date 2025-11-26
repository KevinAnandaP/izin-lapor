package com.izinlapor.dao;

import com.izinlapor.model.ActivityLog;
import com.izinlapor.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ActivityLogDAO {

    public boolean logActivity(ActivityLog log) throws SQLException {
        String sql = "INSERT INTO activity_logs (user_id, report_id, action) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, log.getUserId());
            pstmt.setInt(2, log.getReportId());
            pstmt.setString(3, log.getAction());
            return pstmt.executeUpdate() > 0;
        }
    }
}
