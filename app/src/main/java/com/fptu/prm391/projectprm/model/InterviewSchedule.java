package com.fptu.prm391.projectprm.model;

import java.io.Serializable;

public class InterviewSchedule implements Serializable {
    private int id;
    private int applicationId;
    private String scheduledTime;
    private String status; // Pending, Accepted, Rejected

    // Constructors
    public InterviewSchedule() {}

    public InterviewSchedule(int applicationId, String scheduledTime, String status) {
        this.applicationId = applicationId;
        this.scheduledTime = scheduledTime;
        this.status = status;
    }

    // Getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
