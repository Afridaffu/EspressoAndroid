package com.coyni.mapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    public final String TAG = getClass().getName();

    private static DatabaseHandler sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Coyni";
    //Table Names
    private static final String TABLE_PERMANENT_TOKEN = "tblPermanentToken";
    private static final String TABLE_THUMB_PIN_LOCK = "tblThumbPinLock";
    private static final String TABLE_FACE_PIN_LOCK = "tblFacePinLock";
    private static final String TABLE_USER_DETAILS = "tblUserDetails";
    private static final String TABLE_REMEMBER = "tblRemember";
    private static final String TABLE_DONT_REMIND = "tblDontRemind";
    //Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IS_LOCK = "isLock";
    private static final String COLUMN_PER_TOKEN = "perToken";
    private static final String COLUMN_EMAIL_REMIND = "username";
    private static final String COLUMN_USER_DETAILS = "email";
    private static final String COLUMN_DONT_REMIND = "isDontRemind";


    private DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DatabaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PERMANENT_TOKEN + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + COLUMN_PER_TOKEN + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_THUMB_PIN_LOCK + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + COLUMN_IS_LOCK + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FACE_PIN_LOCK + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + COLUMN_IS_LOCK + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REMEMBER + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + COLUMN_EMAIL_REMIND + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_USER_DETAILS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + COLUMN_USER_DETAILS + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_DONT_REMIND + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 1, " + COLUMN_DONT_REMIND + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Need to handle the Database upgrade scenarios
        //We need to handle the cases where DB tables are altered
    }

    public void insertThumbPinLock(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_LOCK, value);
        db.insert(TABLE_THUMB_PIN_LOCK, null, contentValues);
        db.close();
    }

    public String getThumbPinLock() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_THUMB_PIN_LOCK, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IS_LOCK));
            LogUtils.v(TAG, result);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearThumbPinLockTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_THUMB_PIN_LOCK);
        db.close();
    }

    public void insertPermanentToken(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PER_TOKEN, value);
        long rowId = db.insert(TABLE_PERMANENT_TOKEN, null, contentValues);
        db.close();
    }

    public String getPermanentToken() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_PERMANENT_TOKEN, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PER_TOKEN));
            LogUtils.v(TAG, result);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearPermanentTokenTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_PERMANENT_TOKEN);
        db.close();
    }

    public void insertFacePinLock(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_IS_LOCK, value);
        db.insert(TABLE_FACE_PIN_LOCK, null, contentValues);
        db.close();
    }

    public void clearFacePinLockTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_FACE_PIN_LOCK);
        db.close();
    }

    public String getFacePinLock() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_FACE_PIN_LOCK, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IS_LOCK));
            LogUtils.v(TAG, result);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_THUMB_PIN_LOCK);
        db.execSQL("Delete from " + TABLE_FACE_PIN_LOCK);
        db.execSQL("Delete from " + TABLE_USER_DETAILS);
        db.execSQL("Delete from " + TABLE_REMEMBER);
        db.execSQL("Delete from " + TABLE_DONT_REMIND);
        db.execSQL("Delete from " + TABLE_PERMANENT_TOKEN);
        db.close();
    }

    public void insertTableRemember(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL_REMIND, value);
        db.insert(TABLE_REMEMBER, null, contentValues);
        db.close();
    }


    public String getTableRemember() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_REMEMBER, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL_REMIND));
            LogUtils.v(TAG, result);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearTableRemember() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_REMEMBER);
        db.close();
    }

    public void insertTableUserDetails(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USER_DETAILS, value);
        db.insert(TABLE_USER_DETAILS, null, contentValues);
        db.close();
    }


    public String getTableUserDetails() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_USER_DETAILS, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_DETAILS));
            LogUtils.v(TAG, result);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearTableUserDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_USER_DETAILS);
        db.close();
    }

    public void insertTableDontRemind(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DONT_REMIND, value);
        db.insert(TABLE_DONT_REMIND, null, contentValues);
        db.close();
    }


    public String getTableDontRemind() {
        String result = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_DONT_REMIND, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DONT_REMIND));
            LogUtils.v(TAG, result);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearTableDontRemind() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete from " + TABLE_DONT_REMIND);
        db.close();
    }

    public void initializeDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.close();
    }
}
