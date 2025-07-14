package com.fptu.prm391.projectprm.model;

import java.io.Serializable;

public class Internship implements Serializable {
    private int id;
    private String title;
    private String company;
    private String location;
    private String duration;
    private String field;
    private String description;
    private String requirements;
    private String stipend;
    private String deadline;
    private int recruiterId;
    private String createdAt;
    private double latitude;
    private double longitude;

    public Internship() {}

    public Internship(String title, String company, String location, String duration,
                      String field, String description, String requirements,
                      String stipend, String deadline, int recruiterId,
                      double latitude, double longitude) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.duration = duration;
        this.field = field;
        this.description = description;
        this.requirements = requirements;
        this.stipend = stipend;
        this.deadline = deadline;
        this.recruiterId = recruiterId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRequirements() { return requirements; }
    public void setRequirements(String requirements) { this.requirements = requirements; }

    public String getStipend() { return stipend; }
    public void setStipend(String stipend) { this.stipend = stipend; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public int getRecruiterId() { return recruiterId; }
    public void setRecruiterId(int recruiterId) { this.recruiterId = recruiterId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
