package com.izinlapor.model;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private int reportId;
    private int userId;
    private String message;
    private Timestamp createdAt;
    
    // Extra field for display
    private String userFullName;
    private String userRole;

    public Comment() {}

    public Comment(int id, int reportId, int userId, String message, Timestamp createdAt) {
        this.id = id;
        this.reportId = reportId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    
    @Override
    public String toString() {
        return userFullName + " (" + userRole + "):\n" + message + "\n[" + createdAt + "]";
    }
}
