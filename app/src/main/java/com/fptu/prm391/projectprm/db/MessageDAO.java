package com.fptu.prm391.projectprm.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fptu.prm391.projectprm.model.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public static final String TABLE_NAME = "messages";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_RECEIVER_ID = "receiver_id";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_SENT_AT = "sent_at";
    public static final String COLUMN_READ = "read";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_SENDER_ID + " INTEGER NOT NULL,"
            + COLUMN_RECEIVER_ID + " INTEGER NOT NULL,"
            + COLUMN_CONTENT + " TEXT NOT NULL,"
            + COLUMN_SENT_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
            + COLUMN_READ + " INTEGER DEFAULT 0,"
            + "FOREIGN KEY(" + COLUMN_SENDER_ID + ") REFERENCES " + UserDAO.TABLE_NAME + "(" + UserDAO.COLUMN_ID + "),"
            + "FOREIGN KEY(" + COLUMN_RECEIVER_ID + ") REFERENCES " + UserDAO.TABLE_NAME + "(" + UserDAO.COLUMN_ID + ")"
            + ")";

    private SQLiteDatabase db;

    public MessageDAO(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SENDER_ID, message.getSenderId());
        values.put(COLUMN_RECEIVER_ID, message.getReceiverId());
        values.put(COLUMN_CONTENT, message.getContent());
        values.put(COLUMN_READ, message.isRead() ? 1 : 0);

        return db.insert(TABLE_NAME, null, values);
    }

    public List<Message> getConversation(int user1Id, int user2Id) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE (" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) OR " +
                "(" + COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?) " +
                "ORDER BY " + COLUMN_SENT_AT + " ASC";

        Cursor cursor = db.rawQuery(query,
                new String[]{String.valueOf(user1Id), String.valueOf(user2Id),
                        String.valueOf(user2Id), String.valueOf(user1Id)});

        while (cursor.moveToNext()) {
            Message message = new Message();
            message.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            message.setSenderId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SENDER_ID)));
            message.setReceiverId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RECEIVER_ID)));
            message.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)));
            message.setSentAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENT_AT)));
            message.setRead(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_READ)) == 1);

            messages.add(message);
        }
        cursor.close();
        return messages;
    }

    public int markMessagesAsRead(int senderId, int receiverId) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_READ, 1);

        return db.update(TABLE_NAME, values,
                COLUMN_SENDER_ID + " = ? AND " + COLUMN_RECEIVER_ID + " = ?",
                new String[]{String.valueOf(senderId), String.valueOf(receiverId)});
    }

    public int deleteMessage(int messageId) {
        return db.delete(TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(messageId)});
    }
}