package com.fptu.prm391.projectprm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "internship_recruitment.db";
    private static final int DATABASE_VERSION = 2;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo các bảng
        db.execSQL(UserDAO.CREATE_TABLE);
        db.execSQL(InternshipDAO.CREATE_TABLE);
        db.execSQL(ApplicationDAO.CREATE_TABLE);
        db.execSQL(InterviewDAO.CREATE_TABLE);
        db.execSQL(NotificationDAO.CREATE_TABLE);
        db.execSQL(MessageDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa các bảng cũ nếu tồn tại
        db.execSQL("DROP TABLE IF EXISTS " + UserDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InternshipDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ApplicationDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + InterviewDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MessageDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NotificationDAO.TABLE_NAME);

        // Tạo lại database
        onCreate(db);
    }
}
