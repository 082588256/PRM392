package com.fptu.prm391.projectprm.model;

import java.io.Serializable;

public class Application implements Serializable {
    private int id;
    private int studentId;
    private int internshipId;
    private String resume;
    private String status; // Pending, Under Review, Accepted, Rejected
    private String appliedAt;
    private Internship internship; // For joined query results

    // Constructors
    public Application() {}

    public Application(int studentId, int internshipId, String resume) {
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.resume = resume;
        this.status = "Pending";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(int internshipId) {
        this.internshipId = internshipId;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(String appliedAt) {
        this.appliedAt = appliedAt;
    }

    public Internship getInternship() {
        return internship;
    }

    public void setInternship(Internship internship) {
        this.internship = internship;
    }
}