package com.izinlapor.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private int userId;
    private int reportId;
    private String action;
    private Timestamp timestamp;

    public ActivityLog() {}

    public ActivityLog(int id, int userId, int reportId, String action, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.reportId = reportId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}
