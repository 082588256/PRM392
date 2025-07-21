package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fptu.prm391.projectprm.model.Interview;
import com.fptu.prm391.projectprm.model.InterviewInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InterviewDAO {

    public static final String TABLE_NAME = "interviews";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APPLICATION_ID = "application_id";
    public static final String COLUMN_SCHEDULED_TIME = "scheduled_time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTES = "notes";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_APPLICATION_ID + " INTEGER NOT NULL," +
            COLUMN_SCHEDULED_TIME + " TEXT NOT NULL," +
            COLUMN_STATUS + " TEXT DEFAULT 'Proposed'," +
            COLUMN_NOTES + " TEXT," +
            "FOREIGN KEY(" + COLUMN_APPLICATION_ID + ") REFERENCES " + ApplicationDAO.TABLE_NAME + "(" + ApplicationDAO.COLUMN_ID + ")" +
            ")";

    private SQLiteDatabase db;

    public InterviewDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertInterview(Interview interview) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPLICATION_ID, interview.getApplicationId());

        String scheduledTime = interview.getScheduledTime();
        if (scheduledTime == null) {
            scheduledTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        }

        values.put(COLUMN_SCHEDULED_TIME, scheduledTime);
        values.put(COLUMN_STATUS, interview.getStatus());
        values.put(COLUMN_NOTES, interview.getNotes());

        return db.insert(TABLE_NAME, null, values);
    }

    public List<Interview> getInterviewsByApplicationId(int applicationId) {
        List<Interview> interviews = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_APPLICATION_ID + " = ?",
                new String[]{String.valueOf(applicationId)},
                null, null, COLUMN_SCHEDULED_TIME + " ASC");

        while (cursor.moveToNext()) {
            Interview interview = new Interview();
            interview.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            interview.setApplicationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPLICATION_ID)));
            interview.setScheduledTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULED_TIME)));
            interview.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            interview.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTES)));
            interviews.add(interview);
        }
        cursor.close();
        return interviews;
    }

    public int updateScheduledTimeByApplicationId(int applicationId, String newScheduledTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULED_TIME, newScheduledTime);

        return db.update(TABLE_NAME, values,
                COLUMN_APPLICATION_ID + " = ?",
                new String[]{String.valueOf(applicationId)});
    }

    public boolean updateInterviewTime(int interviewId, String newTime) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULED_TIME, newTime);

        int rows = db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(interviewId)});
        return rows > 0;
    }

    public int updateInterviewStatus(int interviewId, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        return db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(interviewId)});
    }

    public boolean updateInterviewTimeAndStatus(int interviewId, String time, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCHEDULED_TIME, time);
        values.put(COLUMN_STATUS, status);

        int rows = db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(interviewId)});
        return rows > 0;
    }

    public int deleteInterview(int interviewId) {
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(interviewId)});
    }

    public List<InterviewInfo> getInterviewInfoByStudentId(int studentId) {
        List<InterviewInfo> list = new ArrayList<>();

        String sql = "SELECT i.id, u.email, i.scheduled_time, i.status, i.notes, s.company " +
                "FROM interviews i " +
                "JOIN applications a ON i.application_id = a.id " +
                "JOIN users u ON a.student_id = u.id " +
                "JOIN internships s ON a.internship_id = s.id " +
                "WHERE a.student_id = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(studentId)});
        while (cursor.moveToNext()) {
            InterviewInfo info = new InterviewInfo();
            info.setInterviewId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            info.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            info.setScheduledTime(cursor.getString(cursor.getColumnIndexOrThrow("scheduled_time")));
            info.setStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
            info.setNotes(cursor.getString(cursor.getColumnIndexOrThrow("notes")));
            info.setCompany(cursor.getString(cursor.getColumnIndexOrThrow("company")));
            list.add(info);
        }
        cursor.close();
        return list;
    }
}
