package com.example.nida.queuemanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nida on 3/1/2019.
 */

public class SMSHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smslist.db";
    public static final int DATABASE_VERSION = 1;

    public SMSHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SMS_TABLE = "CREATE TABLE " +
                Contact.SMSEntry.TABLE_NAME + " (" +
                Contact.SMSEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contact.SMSEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                Contact.SMSEntry.COLUMN_NUMBER + " VARCHAR NOT NULL, " +
                Contact.SMSEntry.COLUMN_DATE + " DATE NOT NULL, " +
                Contact.SMSEntry.COLUMN_TIME + " TIME NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_SMS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contact.SMSEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
