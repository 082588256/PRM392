package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fptu.prm391.projectprm.model.User;

public class UserDAO {
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_UNIVERSITY = "university";
    public static final String COLUMN_COMPANY = "company";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL,"
            + COLUMN_ROLE + " TEXT NOT NULL,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_UNIVERSITY + " TEXT,"
            + COLUMN_COMPANY + " TEXT"
            + ")";

    private SQLiteDatabase db;

    public UserDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_ROLE, user.getRole());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_UNIVERSITY, user.getUniversity());
        values.put(COLUMN_COMPANY, user.getCompany());

        return db.insert(TABLE_NAME, null, values);
    }

    public User getUserByEmail(String email) {
        User user = null;
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_ROLE,
                        COLUMN_NAME, COLUMN_UNIVERSITY, COLUMN_COMPANY},
                COLUMN_EMAIL + " = ?",
                new String[]{email},
                null, null, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE)));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            user.setUniversity(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIVERSITY)));
            user.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPANY)));
        }
        cursor.close();
        return user;
    }

    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_UNIVERSITY, user.getUniversity());
        values.put(COLUMN_COMPANY, user.getCompany());

        return db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
    }

    public int deleteUser(int userId) {
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }
}