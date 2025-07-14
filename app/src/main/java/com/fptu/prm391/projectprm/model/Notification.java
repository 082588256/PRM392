package com.fptu.prm391.projectprm.model;

public class Notification {
    private int id;
    private int userId;
    private String content;
    private boolean isRead;
    private String createdAt;

    // Constructors
    public Notification() {}

    public Notification(int userId, String content) {
        this.userId = userId;
        this.content = content;
        this.isRead = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
