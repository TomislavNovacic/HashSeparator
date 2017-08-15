package com.example.android.hashseparator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "WebpagesList.db";
    private static final String TABLE_NAME = "Webpages";
    private static final String KEY_URL = "url";
    private static final String KEY_HASH = "hash";
    private static final String KEY_STORAGE = "storage_space";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_URL + " STRING PRIMARY KEY," +
                    KEY_HASH + " TEXT," +
                    KEY_STORAGE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void addWebpage(Webpage page) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_URL, page.getUrl());
        values.put(KEY_HASH, page.getHash());
        values.put(KEY_STORAGE, page.getStorage());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<Webpage> getAllWebpages() {
        ArrayList<Webpage> webpageList = new ArrayList<Webpage>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Webpage webpage = null;
                try {
                    webpage = new Webpage(cursor.getString(0),cursor.getString(1).getBytes("UTF-8"),cursor.getString(2));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                webpageList.add(webpage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return webpageList;
    }
}
