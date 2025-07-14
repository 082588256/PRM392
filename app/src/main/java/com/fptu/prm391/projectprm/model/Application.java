package com.fptu.prm391.projectprm.model;

import java.io.Serializable;

public class Application implements Serializable {
    private int id;
    private int studentId;
    private int internshipId;
    // private String resume;          // <-- XÓA TRƯỜNG NÀY
    private String resumeFile;      // Đường dẫn/uri file CV (.doc/.docx/.pdf)
    private String coverLetter;     // Thư giới thiệu
    private String note;            // Lưu ý (nếu có)
    private String status;          // Pending, Under Review, Accepted, Rejected
    private String appliedAt;
    private Internship internship;  // For joined query results

    // Constructors
    public Application() {}

    // Constructor đầy đủ cho insert nhanh
    public Application(int studentId, int internshipId, String resumeFile, String coverLetter, String note) {
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.resumeFile = resumeFile;
        this.coverLetter = coverLetter;
        this.note = note;
        this.status = "Pending";
    }

    // Constructor đầy đủ tất cả trường
    public Application(int id, int studentId, int internshipId, String resumeFile, String coverLetter, String note, String status, String appliedAt) {
        this.id = id;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.resumeFile = resumeFile;
        this.coverLetter = coverLetter;
        this.note = note;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getInternshipId() { return internshipId; }
    public void setInternshipId(int internshipId) { this.internshipId = internshipId; }

    public String getResumeFile() { return resumeFile; }
    public void setResumeFile(String resumeFile) { this.resumeFile = resumeFile; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppliedAt() { return appliedAt; }
    public void setAppliedAt(String appliedAt) { this.appliedAt = appliedAt; }

    public Internship getInternship() { return internship; }
    public void setInternship(Internship internship) { this.internship = internship; }
}

