package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fptu.prm391.projectprm.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public static final String TABLE_NAME = "notifications";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_IS_READ = "is_read";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_USER_ID + " INTEGER NOT NULL, "
            + COLUMN_CONTENT + " TEXT NOT NULL, "
            + COLUMN_IS_READ + " INTEGER DEFAULT 0, "
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + UserDAO.TABLE_NAME + "(" + UserDAO.COLUMN_ID + ")"
            + ")";

    private final SQLiteDatabase db;

    public NotificationDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertNotification(Notification notification) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, notification.getUserId());
        values.put(COLUMN_CONTENT, notification.getContent());
        values.put(COLUMN_IS_READ, notification.isRead() ? 1 : 0);
        return db.insert(TABLE_NAME, null, values);
    }

    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null,
                COLUMN_USER_ID + " = ? AND " + COLUMN_IS_READ + " = 0",
                new String[]{String.valueOf(userId)}, null, null,
                COLUMN_CREATED_AT + " DESC");

        while (cursor.moveToNext()) {
            Notification n = new Notification();
            n.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            n.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            n.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            n.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_READ)) == 1);
            n.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
            notifications.add(n);
        }
        cursor.close();
        return notifications;
    }

    public int markAllAsRead(int userId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        return db.update(TABLE_NAME, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
    }

    public int deleteNotification(int notificationId) {
        return db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(notificationId)});
    }
}
