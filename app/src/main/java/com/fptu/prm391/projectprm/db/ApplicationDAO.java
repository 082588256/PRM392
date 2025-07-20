package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Internship;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {
    public static final String TABLE_NAME = "applications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_INTERNSHIP_ID = "internship_id";
    public static final String COLUMN_RESUME_FILE = "resume_file";      // Đường dẫn/uri file CV
    public static final String COLUMN_COVER_LETTER = "cover_letter";   // Thư giới thiệu
    public static final String COLUMN_NOTE = "note";                   // Lưu ý
    public static final String COLUMN_STATUS = "status";               // Pending, Under Review, Accepted, Rejected, Withdrawn application
    public static final String COLUMN_APPLIED_AT = "applied_at";       // Thời điểm ứng tuyển

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_STUDENT_ID + " INTEGER NOT NULL, "
            + COLUMN_INTERNSHIP_ID + " INTEGER NOT NULL, "
            + COLUMN_RESUME_FILE + " TEXT, "
            + COLUMN_COVER_LETTER + " TEXT, "
            + COLUMN_NOTE + " TEXT, "
            + COLUMN_STATUS + " TEXT DEFAULT 'Pending', "
            + COLUMN_APPLIED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COLUMN_STUDENT_ID + ") REFERENCES users(id), "
            + "FOREIGN KEY(" + COLUMN_INTERNSHIP_ID + ") REFERENCES internships(id)"
            + ")";

    private SQLiteDatabase db;

    public ApplicationDAO(SQLiteDatabase db) {
        this.db = db;
        Log.d("ApplicationDAO", "Initialized ApplicationDAO with database: " + (db != null ? "valid" : "null"));
    }

    // Thêm ứng tuyển mới
    public long insertApplication(Application application) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, application.getStudentId());
        values.put(COLUMN_INTERNSHIP_ID, application.getInternshipId());
        values.put(COLUMN_RESUME_FILE, application.getResumeFile());
        values.put(COLUMN_COVER_LETTER, application.getCoverLetter());
        values.put(COLUMN_NOTE, application.getNote());
        values.put(COLUMN_STATUS, "Pending");
        long result = db.insert(TABLE_NAME, null, values);
        Log.d("ApplicationDAO", "Insert application for studentId " + application.getStudentId() + ", result: " + result);
        return result;
    }

    // Lấy danh sách ứng tuyển theo studentId
    public List<Application> getApplicationsByStudentId(int studentId) {
        List<Application> applications = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_STUDENT_ID + " = ?",
                new String[]{String.valueOf(studentId)},
                null, null, COLUMN_APPLIED_AT + " DESC");

        while (cursor.moveToNext()) {
            Application application = new Application();
            application.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            application.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
            application.setInternshipId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INTERNSHIP_ID)));
            application.setResumeFile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESUME_FILE)));
            application.setCoverLetter(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COVER_LETTER)));
            application.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)));
            application.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            application.setAppliedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPLIED_AT)));
            applications.add(application);
        }
        cursor.close();
        Log.d("ApplicationDAO", "Fetched " + applications.size() + " applications for studentId " + studentId);
        return applications;
    }

    // Lấy danh sách ứng tuyển với thông tin internship
    public List<Application> getApplicationsWithInternship(int studentId) {
        List<Application> applications = new ArrayList<>();
        String query = "SELECT a.*, " +
                "i." + InternshipDAO.COLUMN_TITLE + ", " +
                "i." + InternshipDAO.COLUMN_COMPANY + ", " +
                "i." + InternshipDAO.COLUMN_LOCATION + ", " +
                "i." + InternshipDAO.COLUMN_DURATION + ", " +
                "i." + InternshipDAO.COLUMN_FIELD + ", " +
                "i." + InternshipDAO.COLUMN_DESCRIPTION + ", " +
                "i." + InternshipDAO.COLUMN_REQUIREMENTS + ", " +
                "i." + InternshipDAO.COLUMN_STIPEND + ", " +
                "i." + InternshipDAO.COLUMN_DEADLINE + ", " +
                "i." + InternshipDAO.COLUMN_RECRUITER_ID + ", " +
                "i." + InternshipDAO.COLUMN_LATITUDE + ", " +
                "i." + InternshipDAO.COLUMN_LONGITUDE + ", " +
                "i." + InternshipDAO.COLUMN_CREATED_AT +
                " FROM " + TABLE_NAME + " a " +
                " INNER JOIN " + InternshipDAO.TABLE_NAME + " i " +
                " ON a." + COLUMN_INTERNSHIP_ID + " = i." + InternshipDAO.COLUMN_ID +
                " WHERE a." + COLUMN_STUDENT_ID + " = ?" +
                " ORDER BY a." + COLUMN_APPLIED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});

        while (cursor.moveToNext()) {
            Application application = new Application();
            application.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            application.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
            application.setInternshipId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INTERNSHIP_ID)));
            application.setResumeFile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESUME_FILE)));
            application.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            application.setAppliedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPLIED_AT)));

            // Gán đối tượng Internship từ JOIN
            Internship internship = new Internship();
            internship.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INTERNSHIP_ID)));
            internship.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_TITLE)));
            internship.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_COMPANY)));
            internship.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_LOCATION)));
            internship.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_DURATION)));
            internship.setField(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_FIELD)));
            internship.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_DESCRIPTION)));
            internship.setRequirements(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_REQUIREMENTS)));
            internship.setStipend(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_STIPEND)));
            internship.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_DEADLINE)));
            internship.setRecruiterId(cursor.getInt(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_RECRUITER_ID)));
            internship.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_LATITUDE)));
            internship.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_LONGITUDE)));
            internship.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(InternshipDAO.COLUMN_CREATED_AT)));

            application.setInternship(internship);
            applications.add(application);
        }

        cursor.close();
        Log.d("ApplicationDAO", "Fetched " + applications.size() + " applications with internship for studentId " + studentId);
        return applications;
    }

    // Cập nhật trạng thái ứng tuyển
    public int updateApplicationStatus(int applicationId, String status) {
        try {
            Log.d("ApplicationDAO", "Attempting to update status for applicationId: " + applicationId + " to: " + status);
            // Kiểm tra trạng thái hiện tại
            Application application = getApplicationById(applicationId);
            if (application == null) {
                Log.e("ApplicationDAO", "Application not found for id: " + applicationId);
                return 0;
            }
            String currentStatus = application.getStatus();
            Log.d("ApplicationDAO", "Current status for applicationId " + applicationId + ": " + currentStatus);
            if (!currentStatus.equals("Pending") && !currentStatus.equals("Under Review")) {
                Log.w("ApplicationDAO", "Cannot update status for applicationId " + applicationId + ": Current status is " + currentStatus);
                return 0;
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, status);
            int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(applicationId)});
            Log.d("ApplicationDAO", "Update status for applicationId " + applicationId + " to " + status + ", result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("ApplicationDAO", "Error updating status for applicationId " + applicationId + ": " + e.getMessage());
            return 0;
        }
    }

    // Xóa ứng tuyển
    public int deleteApplication(int applicationId) {
        try {
            Log.d("ApplicationDAO", "Attempting to delete applicationId: " + applicationId);
            int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(applicationId)});
            Log.d("ApplicationDAO", "Delete applicationId " + applicationId + ", result: " + result);
            return result;
        } catch (Exception e) {
            Log.e("ApplicationDAO", "Error deleting applicationId " + applicationId + ": " + e.getMessage());
            return 0;
        }
    }

    // Lấy Application theo ID
    public Application getApplicationById(int id) {
        Log.d("ApplicationDAO", "Fetching application for id: " + id);
        Application application = null;
        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            application = new Application();
            application.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            application.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_ID)));
            application.setInternshipId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INTERNSHIP_ID)));
            application.setResumeFile(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESUME_FILE)));
            application.setCoverLetter(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COVER_LETTER)));
            application.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE)));
            application.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            application.setAppliedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPLIED_AT)));
        }
        cursor.close();
        Log.d("ApplicationDAO", "Fetched application for id " + id + ", found: " + (application != null));
        return application;
    }

    // Phương thức debug để kiểm tra trạng thái
    public void debugApplication(int applicationId) {
        Log.d("ApplicationDAO", "Debugging application for id: " + applicationId);
        Application app = getApplicationById(applicationId);
        if (app != null) {
            Log.d("ApplicationDAO", "Application: id=" + app.getId() + ", status=" + app.getStatus() + ", studentId=" + app.getStudentId() + ", internshipId=" + app.getInternshipId());
        } else {
            Log.d("ApplicationDAO", "Application not found for id=" + applicationId);
        }
    }

    public boolean hasApplied(int studentId, int internshipId) {
        Log.d("ApplicationDAO", "Checking hasApplied for studentId=" + studentId + ", internshipId=" + internshipId);
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID},
                COLUMN_STUDENT_ID + " = ? AND " + COLUMN_INTERNSHIP_ID + " = ?",
                new String[]{String.valueOf(studentId), String.valueOf(internshipId)},
                null, null, null);
        int count = cursor.getCount();
        boolean hasApplied = count > 0;
        Log.d("ApplicationDAO", "Has applied: " + hasApplied + ", count=" + count);
        cursor.close();
        return hasApplied;
    }
}