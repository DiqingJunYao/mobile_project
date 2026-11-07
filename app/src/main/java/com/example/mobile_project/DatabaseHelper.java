package com.example.mobile_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Contacts.db";
    public static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME_CONTACTS = "contacts";
    public static final String CONTACT_COLUMN_ID = "id";
    public static final String CONTACT_COLUMN_FIRST_NAME = "first_name";
    public static final String CONTACT_COLUMN_LAST_NAME = "last_name";
    public static final String CONTACT_COLUMN_COUNTRY_CODE = "country_code";
    public static final String CONTACT_COLUMN_PHONE = "phone";
    public static final String CONTACT_COLUMN_EMAIL = "email";
    public static final String CONTACT_COLUMN_NICKNAME = "nickname";
    public static final String CONTACT_COLUMN_AVATAR = "avatar";
    public static final String TABLE_NAME_MESSAGES = "messages";
    public static final String MESSAGE_COLUMN_ID = "id";
    public static final String MESSAGE_COLUMN_CONTACT_ID = "contact_id";
    public static final String MESSAGE_COLUMN_CONTENT = "content";
    public static final String MESSAGE_COLUMN_TIMESTAMP = "timestamp";
    public static final String MESSAGE_COLUMN_IS_SENT = "is_sent";
    public static final String MESSAGE_COLUMN_CONTACT_NAME = "contact_name";
    public static final String MESSAGE_COLUMN_MESSAGE_STATUS = "message_status";
    public static final String TABLE_NAME_MULTIPART_MESSAGES = "multipart_messages";
    public static final String MULTIPART_MESSAGE_COLUMN_MESSAGE_ID = "id";
    public static final String MULTIPART_MESSAGE_COLUMN_PART_INDEX = "part_index";
    public static final String MULTIPART_MESSAGE_COLUMN_PART_STATUS = "part_status";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME_CONTACTS + "(" +
                CONTACT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CONTACT_COLUMN_FIRST_NAME + " TEXT," +
                CONTACT_COLUMN_LAST_NAME + " TEXT," +
                CONTACT_COLUMN_COUNTRY_CODE + " TEXT," +
                CONTACT_COLUMN_PHONE + " TEXT," +
                CONTACT_COLUMN_EMAIL + " TEXT," +
                CONTACT_COLUMN_NICKNAME + " TEXT," +
                CONTACT_COLUMN_AVATAR + " TEXT" +
                ")";
        db.execSQL(createTableQuery);
        String createMessagesTable = "CREATE TABLE " + TABLE_NAME_MESSAGES + "(" +
                MESSAGE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MESSAGE_COLUMN_CONTACT_ID + " INTEGER NOT NULL," +
                MESSAGE_COLUMN_CONTENT + " TEXT NOT NULL," +
                MESSAGE_COLUMN_TIMESTAMP + " INTEGER NOT NULL," +
                MESSAGE_COLUMN_IS_SENT + " INTEGER NOT NULL," +
                MESSAGE_COLUMN_CONTACT_NAME + " TEXT NOT NULL," +
                MESSAGE_COLUMN_MESSAGE_STATUS + " TEXT NOT NULL," +
                "FOREIGN KEY(" + MESSAGE_COLUMN_CONTACT_ID + ") REFERENCES " +
                TABLE_NAME_CONTACTS + "(" + CONTACT_COLUMN_ID + ")" + " ON DELETE CASCADE" +
                ")";
        db.execSQL(createMessagesTable);
        String createMultipartMessagesTable = "CREATE TABLE " + TABLE_NAME_MULTIPART_MESSAGES + "(" +
                MULTIPART_MESSAGE_COLUMN_MESSAGE_ID + " INTEGER NOT NULL," +
                MULTIPART_MESSAGE_COLUMN_PART_INDEX + " INTEGER NOT NULL," +
                MULTIPART_MESSAGE_COLUMN_PART_STATUS + " TEXT NOT NULL," +
                "PRIMARY KEY (" + MULTIPART_MESSAGE_COLUMN_MESSAGE_ID + ", " + MULTIPART_MESSAGE_COLUMN_PART_INDEX + "), " +
                "FOREIGN KEY(" + MULTIPART_MESSAGE_COLUMN_MESSAGE_ID + ") REFERENCES " +
                TABLE_NAME_MESSAGES + "(" + MESSAGE_COLUMN_ID + ")" + " ON DELETE CASCADE" +
                ")";
        db.execSQL(createMultipartMessagesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE contacts ADD COLUMN address TEXT");
        }
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    public long insertContact(String firstName, String lastName,String countryCode, String phone, String email, String nickname, String avatar) {
        if (phone == null)
            return -1;
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("country_code", countryCode);
        values.put("phone", phone);
        values.put("email", email);
        values.put("nickname", nickname);
        values.put("avatar", avatar);
        long result = db.insert(TABLE_NAME_CONTACTS, null, values);
        db.close();
        return result;
    }

    public ArrayList<Contact> getAllContacts() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Contact> contacts = new ArrayList<>();
        String query = "SELECT * FROM contacts";
        android.database.Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_LAST_NAME));
            String countryCode = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_COUNTRY_CODE));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_EMAIL));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_AVATAR));
            contacts.add(new Contact(firstName, lastName, nickname, countryCode, phone, email, avatar));
        }
        cursor.close();
        db.close();
        return contacts;
    }

    public Contact getContactById(long id) {
        Contact contact = null;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM contacts WHERE id = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_LAST_NAME));
            String countryCode = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_COUNTRY_CODE));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_EMAIL));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_AVATAR));
            if (firstName != null && lastName != null && phone != null)
                contact = new Contact(firstName, lastName, nickname, countryCode, phone, email, avatar);
        }
        cursor.close();
        db.close();
        return contact;
    }

    public Contact getContactByNumber(String countryCode, String number) {
        Contact contact = null;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM contacts WHERE country_code = ? AND phone = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{countryCode, number});
        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_LAST_NAME));
            String countryCode1 = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_COUNTRY_CODE));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_PHONE));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_EMAIL));
            String nickname = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_NICKNAME));
            String avatar = cursor.getString(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_AVATAR));
            contact = new Contact(firstName, lastName, nickname, countryCode1, phone, email, avatar);
        }
        cursor.close();
        db.close();
        return contact;
    }

    public int getContactIdByNumber(String countryCode, String number) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT id FROM contacts WHERE country_code = ? AND phone = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{countryCode, number});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(CONTACT_COLUMN_ID));
        }
        cursor.close();
        db.close();
        return id;
    }

    public int updateContact(long id, String firstName, String lastName,String countryCode, String phone, String email, String nickname, String avatar) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CONTACT_COLUMN_FIRST_NAME, firstName);
        values.put(CONTACT_COLUMN_LAST_NAME, lastName);
        values.put(CONTACT_COLUMN_COUNTRY_CODE, countryCode);
        values.put(CONTACT_COLUMN_PHONE, phone);
        values.put(CONTACT_COLUMN_EMAIL, email);
        values.put(CONTACT_COLUMN_NICKNAME, nickname);
        values.put(CONTACT_COLUMN_AVATAR, avatar);
        int result = db.update(TABLE_NAME_CONTACTS, values, CONTACT_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public int deleteContact(long id) {
        SQLiteDatabase db = getWritableDatabase();
        int result = db.delete(TABLE_NAME_CONTACTS, CONTACT_COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public ArrayList<Message> getMessagesByContactId(long contactId) {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM messages WHERE contact_id = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(contactId)});
        while (cursor.moveToNext()) {
            String content = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_CONTENT));
            long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_TIMESTAMP));
            boolean isSent = cursor.getInt(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_IS_SENT)) == 1;
            String contactName = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_CONTACT_NAME));
            String messageStatus = cursor.getString(cursor.getColumnIndexOrThrow(MESSAGE_COLUMN_MESSAGE_STATUS));
            Message temp = new Message(isSent, timestamp, content, contactName, messageStatus);
            messages.add(temp);
        }
        cursor.close();
        db.close();
        return messages;
    }

    public long insertMessage(long contactId, String content, long timestamp, boolean isSent, String contactName, String messageStatus
    ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MESSAGE_COLUMN_CONTACT_ID, contactId);
        values.put(MESSAGE_COLUMN_CONTENT, content);
        values.put(MESSAGE_COLUMN_TIMESTAMP, timestamp);
        values.put(MESSAGE_COLUMN_IS_SENT, isSent ? 1 : 0);
        values.put(MESSAGE_COLUMN_CONTACT_NAME, contactName);
        values.put(MESSAGE_COLUMN_MESSAGE_STATUS, messageStatus);
        long messageId = db.insert(TABLE_NAME_MESSAGES, null, values);
        db.close();
        return messageId;
    }

    public void updateMessageStatus(long messageId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("message_status", newStatus);
        int rowsAffected = db.update(
                "messages",
                values,
                "id = ?",
                new String[]{String.valueOf(messageId)}
        );
        Log.d("DATABASE", "Updated rows: " + rowsAffected);
    }

    public long insertMultipartMessage(long messageId, int partIndex, String partStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", messageId);
        values.put("part_index", partIndex);
        values.put("part_status", partStatus);
        long result = db.insert("multipart_messages", null, values);
        db.close();
        Log.d("DATABASE", "Inserted row: " + result);
        return result;
    }
    public long updateMultipartMessageStatus(long messageId, int partIndex, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("part_status", newStatus);
        int rowsAffected = db.update(
                "multipart_messages",
                values,
                "id = ? AND part_index = ?",
                new String[]{String.valueOf(messageId), String.valueOf(partIndex)}
        );
        db.close();
        Log.d("DATABASE", "Updated rows: " + rowsAffected);
        return rowsAffected;
    }
    public boolean checkMultipartInserted(long messageId, int totalParts) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM multipart_messages WHERE id = ?";
        android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(messageId)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == totalParts;
    }
    public void updateOverallMessages(long messageId) {
        String query =
                "SELECT " + "COUNT(*) AS total, " +
                        "SUM(CASE WHEN part_status = 'sent' THEN 1 ELSE 0 END) AS sent_count, " +
                        "SUM(CASE WHEN part_status = 'failed' THEN 1 ELSE 0 END) AS failed_count " +
                        "FROM multipart_messages WHERE id = ?";
        try (SQLiteDatabase db = getWritableDatabase(); android.database.Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(messageId)})) {
            if (!cursor.moveToFirst()) {
                return;
            }

            int total = cursor.getInt(0);
            int sentCount = cursor.getInt(1);
            int failedCount = cursor.getInt(2);

            String status;
            if (total == 0) {
                return;
            } else if (failedCount > 0) {
                status = "failed";
            } else if (sentCount == total) {
                status = "sent";
            } else {
                status = "pending";
            }

            ContentValues values = new ContentValues();
            values.put(MESSAGE_COLUMN_MESSAGE_STATUS, status);
            db.update(
                    TABLE_NAME_MESSAGES,
                    values,
                    MESSAGE_COLUMN_ID + " = ?",
                    new String[]{String.valueOf(messageId)}
            );
            Log.d("DATABASE", "Message " + messageId + " status: " + status);
        }
    }
}
