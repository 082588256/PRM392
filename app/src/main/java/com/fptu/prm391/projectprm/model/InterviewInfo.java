package com.fptu.prm391.projectprm.model;

public class InterviewInfo {
    private int interviewId;
    private String email;
    private String scheduledTime;
    private String status;
    private String notes;
    private String company;
    private int studentId; // ✅ Thêm studentId

    public InterviewInfo() {}
    private int applicationId;

    public int getApplicationId() {
        return applicationId;
    }
    // ✅ Constructor đầy đủ
    public InterviewInfo(int interviewId, String email, String scheduledTime, String status, String notes, String company, int studentId) {
        this.interviewId = interviewId;
        this.email = email;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.notes = notes;
        this.company = company;
        this.studentId = studentId;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    // ✅ Getter & Setter cho studentId
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }
}
