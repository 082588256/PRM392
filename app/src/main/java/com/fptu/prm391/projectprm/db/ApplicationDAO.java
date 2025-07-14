package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Internship;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {
    public static final String TABLE_NAME = "applications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STUDENT_ID = "student_id";
    public static final String COLUMN_INTERNSHIP_ID = "internship_id";
    public static final String COLUMN_RESUME = "resume";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_APPLIED_AT = "applied_at";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_STUDENT_ID + " INTEGER NOT NULL,"
            + COLUMN_INTERNSHIP_ID + " INTEGER NOT NULL,"
            + COLUMN_RESUME + " TEXT NOT NULL,"
            + COLUMN_STATUS + " TEXT DEFAULT 'Pending',"
            + COLUMN_APPLIED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_STUDENT_ID + ") REFERENCES " + UserDAO.TABLE_NAME + "(" + UserDAO.COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_INTERNSHIP_ID + ") REFERENCES " + InternshipDAO.TABLE_NAME + "(" + InternshipDAO.COLUMN_ID + ")"
            + ")";

    private SQLiteDatabase db;

    public ApplicationDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertApplication(Application application) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STUDENT_ID, application.getStudentId());
        values.put(COLUMN_INTERNSHIP_ID, application.getInternshipId());
        values.put(COLUMN_RESUME, application.getResume());
        values.put(COLUMN_STATUS, application.getStatus());

        return db.insert(TABLE_NAME, null, values);
    }

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
            application.setResume(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESUME)));
            application.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            application.setAppliedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_APPLIED_AT)));

            applications.add(application);
        }
        cursor.close();
        return applications;
    }

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
            application.setResume(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESUME)));
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
        return applications;
    }

    public int updateApplicationStatus(int applicationId, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        return db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(applicationId)});
    }

    public int deleteApplication(int applicationId) {
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(applicationId)});
    }
}