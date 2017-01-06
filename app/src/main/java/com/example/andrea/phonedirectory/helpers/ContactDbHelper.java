package com.example.andrea.phonedirectory.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.andrea.phonedirectory.model.Contact;

import java.util.ArrayList;

/**
 * Created by andrea on 03/01/17.
 */
public class ContactDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyContacts.db";
    public static final String CONTACTS_TABLE_NAME = "contacts";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_SURNAME = "surname";
    public static final String CONTACTS_COLUMN_NUMBER = "number";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CONTACTS_TABLE_NAME + " (" +
                    CONTACTS_COLUMN_ID + " INTEGER PRIMARY KEY," +
                    CONTACTS_COLUMN_NAME + " TEXT," +
                    CONTACTS_COLUMN_SURNAME + " TEXT, " +
                    CONTACTS_COLUMN_NUMBER + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME;

    public ContactDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertContact(Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTACTS_COLUMN_NAME, c.getName());
        values.put(CONTACTS_COLUMN_SURNAME, c.getSurname());
        values.put(CONTACTS_COLUMN_NUMBER, c.getNumber());

        // return the created ID
        return db.insert(CONTACTS_TABLE_NAME, null, values);
    }

    public boolean updateContact(Contact c) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CONTACTS_COLUMN_NAME, c.getName());
        values.put(CONTACTS_COLUMN_SURNAME, c.getSurname());
        values.put(CONTACTS_COLUMN_NUMBER, c.getNumber());

        db.update(CONTACTS_TABLE_NAME, values, CONTACTS_COLUMN_ID + " = ? ", new String[] { c.getId().toString() } );
        return true;
    }

    public ArrayList<Contact> readAllContact() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                CONTACTS_COLUMN_ID,
                CONTACTS_COLUMN_NAME,
                CONTACTS_COLUMN_SURNAME,
                CONTACTS_COLUMN_NUMBER
        };

        Cursor cursor = db.query(CONTACTS_TABLE_NAME, projection, null, null, null, null, null);

        ArrayList<Contact> contacts = new ArrayList<>();
        while(cursor.moveToNext()) {
            Contact c = new Contact();
            c.setId(cursor.getLong(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_ID)));
            c.setName(cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_NAME)));
            c.setSurname(cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_SURNAME)));
            c.setNumber(cursor.getString(cursor.getColumnIndexOrThrow(CONTACTS_COLUMN_NUMBER)));
            contacts.add(c);
        }
        cursor.close();

        return contacts;
    }
}
