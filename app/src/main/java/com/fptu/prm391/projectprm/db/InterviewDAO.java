package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fptu.prm391.projectprm.model.Interview;
import com.fptu.prm391.projectprm.model.InterviewInfo;

import java.util.ArrayList;
import java.util.List;

public class InterviewDAO {
    public static final String TABLE_NAME = "interviews";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APPLICATION_ID = "application_id";
    public static final String COLUMN_SCHEDULED_TIME = "scheduled_time";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_NOTES = "notes";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_APPLICATION_ID + " INTEGER NOT NULL,"
            + COLUMN_SCHEDULED_TIME + " TEXT NOT NULL,"
            + COLUMN_STATUS + " TEXT DEFAULT 'Proposed',"
            + COLUMN_NOTES + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_APPLICATION_ID + ") REFERENCES " + ApplicationDAO.TABLE_NAME + "(" + ApplicationDAO.COLUMN_ID + ")"
            + ")";

    private SQLiteDatabase db;

    public InterviewDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Thêm lịch phỏng vấn mới
    public long insertInterview(Interview interview) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPLICATION_ID, interview.getApplicationId());
        values.put(COLUMN_SCHEDULED_TIME, interview.getScheduledTime());
        values.put(COLUMN_STATUS, interview.getStatus());
        values.put(COLUMN_NOTES, interview.getNotes());

        return db.insert(TABLE_NAME, null, values);
    }

    // Lấy danh sách phỏng vấn theo applicationId
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

    // Update trạng thái lịch phỏng vấn
    public int updateInterviewStatus(int interviewId, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);

        return db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(interviewId)});
    }

    // Xóa lịch phỏng vấn
    public int deleteInterview(int interviewId) {
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(interviewId)});
    }

    // ---- CODE THÊM MỚI: Lấy danh sách lịch phỏng vấn + email + company (JOIN) ----
    // Trả về list<InterviewInfo>
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
