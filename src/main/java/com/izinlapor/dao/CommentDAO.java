package com.izinlapor.dao;

import com.izinlapor.model.Comment;
import com.izinlapor.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {

    public boolean addComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO report_comments (report_id, user_id, message) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, comment.getReportId());
            pstmt.setInt(2, comment.getUserId());
            pstmt.setString(3, comment.getMessage());
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Comment> getCommentsByReportId(int reportId) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name, u.role FROM report_comments c " +
                     "JOIN users u ON c.user_id = u.id " +
                     "WHERE c.report_id = ? ORDER BY c.created_at ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, reportId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setId(rs.getInt("id"));
                    comment.setReportId(rs.getInt("report_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setMessage(rs.getString("message"));
                    comment.setCreatedAt(rs.getTimestamp("created_at"));
                    comment.setUserFullName(rs.getString("full_name"));
                    comment.setUserRole(rs.getString("role"));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }
}
