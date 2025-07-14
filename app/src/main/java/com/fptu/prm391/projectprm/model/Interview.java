package com.fptu.prm391.projectprm.model;

import java.io.Serializable;

public class Interview implements Serializable {
    private int id;
    private int applicationId;
    private String scheduledTime;
    private String status; // Proposed, Confirmed, Declined
    private String notes;
    private Application application; // For joined query results

    // Constructors
    public Interview() {}

    public Interview(int applicationId, String scheduledTime) {
        this.applicationId = applicationId;
        this.scheduledTime = scheduledTime;
        this.status = "Proposed";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }
}