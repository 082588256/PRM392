package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fptu.prm391.projectprm.model.InterviewSchedule;

import java.util.ArrayList;
import java.util.List;

public class InterviewScheduleDAO {
    public static final String TABLE_NAME = "interview_schedules";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_APPLICATION_ID = "application_id";
    public static final String COLUMN_SCHEDULED_TIME = "scheduled_time";
    public static final String COLUMN_STATUS = "status";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_APPLICATION_ID + " INTEGER NOT NULL,"
            + COLUMN_SCHEDULED_TIME + " TEXT NOT NULL,"
            + COLUMN_STATUS + " TEXT DEFAULT 'Pending',"
            + "FOREIGN KEY(" + COLUMN_APPLICATION_ID + ") REFERENCES applications(id)"
            + ")";

    private SQLiteDatabase db;

    public InterviewScheduleDAO(SQLiteDatabase db) {
        this.db = db;
    }

    // Insert
    public long insert(InterviewSchedule schedule) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_APPLICATION_ID, schedule.getApplicationId());
        values.put(COLUMN_SCHEDULED_TIME, schedule.getScheduledTime());
        values.put(COLUMN_STATUS, schedule.getStatus());
        return db.insert(TABLE_NAME, null, values);
    }

    // Get schedules by applicationId
    public List<InterviewSchedule> getByApplicationId(int applicationId) {
        List<InterviewSchedule> schedules = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_APPLICATION_ID + " = ?",
                new String[]{String.valueOf(applicationId)},
                null, null, COLUMN_SCHEDULED_TIME + " ASC");

        while (cursor.moveToNext()) {
            InterviewSchedule schedule = new InterviewSchedule();
            schedule.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            schedule.setApplicationId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_APPLICATION_ID)));
            schedule.setScheduledTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHEDULED_TIME)));
            schedule.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS)));
            schedules.add(schedule);
        }
        cursor.close();
        return schedules;
    }

    // Update status
    public int updateStatus(int scheduleId, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        return db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(scheduleId)});
    }

    // Delete schedule
    public int delete(int scheduleId) {
        return db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(scheduleId)});
    }
}
