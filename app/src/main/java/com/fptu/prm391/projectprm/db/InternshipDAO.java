package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fptu.prm391.projectprm.model.Internship;
import java.util.ArrayList;
import java.util.List;

public class InternshipDAO {
    public static final String TABLE_NAME = "internships";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_COMPANY = "company";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DURATION = "duration";
    public static final String COLUMN_FIELD = "field";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_REQUIREMENTS = "requirements";
    public static final String COLUMN_STIPEND = "stipend";
    public static final String COLUMN_DEADLINE = "deadline";
    public static final String COLUMN_RECRUITER_ID = "recruiter_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_CREATED_AT = "created_at";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TITLE + " TEXT NOT NULL,"
            + COLUMN_COMPANY + " TEXT NOT NULL,"
            + COLUMN_LOCATION + " TEXT NOT NULL,"
            + COLUMN_DURATION + " TEXT NOT NULL,"
            + COLUMN_FIELD + " TEXT NOT NULL,"
            + COLUMN_DESCRIPTION + " TEXT NOT NULL,"
            + COLUMN_REQUIREMENTS + " TEXT NOT NULL,"
            + COLUMN_STIPEND + " TEXT,"
            + COLUMN_DEADLINE + " TEXT NOT NULL,"
            + COLUMN_RECRUITER_ID + " INTEGER NOT NULL,"
            + COLUMN_LATITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL,"
            + COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + "FOREIGN KEY(" + COLUMN_RECRUITER_ID + ") REFERENCES " + UserDAO.TABLE_NAME + "(" + UserDAO.COLUMN_ID + ")"
            + ")";

    private SQLiteDatabase db;

    public InternshipDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertInternship(Internship internship) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, internship.getTitle());
        values.put(COLUMN_COMPANY, internship.getCompany());
        values.put(COLUMN_LOCATION, internship.getLocation());
        values.put(COLUMN_DURATION, internship.getDuration());
        values.put(COLUMN_FIELD, internship.getField());
        values.put(COLUMN_DESCRIPTION, internship.getDescription());
        values.put(COLUMN_REQUIREMENTS, internship.getRequirements());
        values.put(COLUMN_STIPEND, internship.getStipend());
        values.put(COLUMN_DEADLINE, internship.getDeadline());
        values.put(COLUMN_RECRUITER_ID, internship.getRecruiterId());
        values.put(COLUMN_LATITUDE, internship.getLatitude());
        values.put(COLUMN_LONGITUDE, internship.getLongitude());

        return db.insert(TABLE_NAME, null, values);
    }

    public List<Internship> getAllInternships() {
        List<Internship> internships = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,
                null, null, null, null, null, COLUMN_CREATED_AT + " DESC");

        while (cursor.moveToNext()) {
            Internship internship = new Internship();
            internship.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            internship.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            internship.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPANY)));
            internship.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
            internship.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
            internship.setField(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIELD)));
            internship.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            internship.setRequirements(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUIREMENTS)));
            internship.setStipend(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STIPEND)));
            internship.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
            internship.setRecruiterId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECRUITER_ID)));
            internship.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)));
            internship.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)));
            internship.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

            internships.add(internship);
        }
        cursor.close();
        return internships;
    }

    public Internship getInternshipById(int id) {
        Internship internship = null;
        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null);

        if (cursor.moveToFirst()) {
            internship = new Internship();
            internship.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            internship.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            internship.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPANY)));
            internship.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
            internship.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
            internship.setField(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIELD)));
            internship.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            internship.setRequirements(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUIREMENTS)));
            internship.setStipend(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STIPEND)));
            internship.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
            internship.setRecruiterId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECRUITER_ID)));
            internship.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)));
            internship.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)));
            internship.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));
        }
        cursor.close();
        return internship;
    }

    public List<Internship> getInternshipsByRecruiterId(int recruiterId) {
        List<Internship> internships = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME,
                null,
                COLUMN_RECRUITER_ID + " = ?",
                new String[]{String.valueOf(recruiterId)},
                null, null, COLUMN_CREATED_AT + " DESC");

        while (cursor.moveToNext()) {
            Internship internship = new Internship();
            internship.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            internship.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
            internship.setCompany(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COMPANY)));
            internship.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
            internship.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)));
            internship.setField(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIELD)));
            internship.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            internship.setRequirements(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUIREMENTS)));
            internship.setStipend(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STIPEND)));
            internship.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
            internship.setRecruiterId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECRUITER_ID)));
            internship.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE)));
            internship.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE)));
            internship.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT)));

            internships.add(internship);
        }
        cursor.close();
        return internships;
    }

    public int updateInternship(Internship internship) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, internship.getTitle());
        values.put(COLUMN_COMPANY, internship.getCompany());
        values.put(COLUMN_LOCATION, internship.getLocation());
        values.put(COLUMN_DURATION, internship.getDuration());
        values.put(COLUMN_FIELD, internship.getField());
        values.put(COLUMN_DESCRIPTION, internship.getDescription());
        values.put(COLUMN_REQUIREMENTS, internship.getRequirements());
        values.put(COLUMN_STIPEND, internship.getStipend());
        values.put(COLUMN_DEADLINE, internship.getDeadline());
        values.put(COLUMN_RECRUITER_ID, internship.getRecruiterId());
        values.put(COLUMN_LATITUDE, internship.getLatitude());
        values.put(COLUMN_LONGITUDE, internship.getLongitude());

        return db.update(TABLE_NAME, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(internship.getId())});
    }

    public int deleteInternship(int id) {
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}
