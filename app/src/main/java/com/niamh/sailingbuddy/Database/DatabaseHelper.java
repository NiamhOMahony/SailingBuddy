package com.niamh.sailingbuddy.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.niamh.sailingbuddy.Utils.Config;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper databaseHelper;

    // All Static variables
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = Config.DATABASE_NAME;

    // Constructor
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context) {
        if(databaseHelper==null){
            synchronized (DatabaseHelper.class) {
                if(databaseHelper==null)
                    databaseHelper = new DatabaseHelper(context);
            }
        }
        return databaseHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables SQL execution
        String CREATE_SAFETY_TABLE = "CREATE TABLE " + Config.TABLE_SAFETY + "("
                + Config.COLUMN_SAFETY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_SAFETY_TYPE + " TEXT NOT NULL, "
                + Config.COLUMN_SAFETY_DESCRIPTION + " TEXT, "
                + Config.COLUMN_SAFETY_AVAILABLE + " TEXT, "
                + Config.COLUMN_SAFETY_AVAILUSER + " TEXT, "
                + Config.COLUMN_SAFETY_FAULT + " TEXT, "
                + Config.COLUMN_SAFETY_FAULTUSER + " TEXT, "
                + Config.COLUMN_SAFETY_FAULTDES + " TEXT, "
                + Config.COLUMN_SAFETY_IMAGE + " BLOB "
                + ")";

        String CREATE_SAILING_TABLE = "CREATE TABLE " + Config.TABLE_SAILING + "("
                + Config.COLUMN_SAILING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_SAILING_TYPE + " TEXT NOT NULL, "
                + Config.COLUMN_SAILING_DESCRIPTION + " TEXT, "
                + Config.COLUMN_SAILING_AVAILABLE + " TEXT, "
                + Config.COLUMN_SAILING_AVAILUSER + " TEXT, "
                + Config.COLUMN_SAILING_FAULT + " TEXT, "
                + Config.COLUMN_SAILING_FAULTUSER + " TEXT, "
                + Config.COLUMN_SAILING_FAULTDES + " TEXT, "
                + Config.COLUMN_SAILING_IMAGE + " BLOB "
                + ")";

        String CREATE_USER_TABLE = "CREATE TABLE " + Config.TABLE_USER + "("
                + Config.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_USER_TYPE + " TEXT NOT NULL, "
                + Config.COLUMN_USER_NAME + " TEXT, "
                + Config.COLUMN_USER_EMAIL + " TEXT, "
                + Config.COLUMN_USER_MOBILE + " TEXT, "
                + Config.COLUMN_USER_PASSSWORD + " TEXT, "
                + Config.COLUMN_USER_IMAGE + " BLOB "
                + ")";

        // Create session SQL execution
        String CREATE_SESSION_TABLE = "CREATE TABLE " + Config.TABLE_SESSION + "("
                + Config.COLUMN_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_SESSION_INSTRUCTOR + " TEXT NOT NULL, "
                + Config.COLUMN_SESSION_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + Config.COLUMN_SESSION_LEVEL + " TEXT, "
                + Config.COLUMN_SESSION_STUDENTS + " TEXT, "
                + Config.COLUMN_SESSION_LAUNCH + " TEXT, "
                + Config.COLUMN_SESSION_RECOVERY + " TEXT, "
                + Config.COLUMN_SESSION_LAND + " TEXT, "
                + Config.COLUMN_SESSION_WATER + " TEXT, "
                + Config.COLUMN_SESSION_AREA + " TEXT, "
                + Config.COLUMN_SESSION_HIGH + " TEXT, "
                + Config.COLUMN_SESSION_LOW + " TEXT, "
                + Config.COLUMN_SESSION_WEATHER + " TEXT "
                + ")";

        // Create tables SQL execution
        String CREATE_NOTES_TABLE = "CREATE TABLE " + Config.TABLE_NOTES + "("
                + Config.COLUMN_NOTES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_NOTES_TITLE + " TEXT NOT NULL, "
                + Config.COLUMN_NOTES_SUBTITLE + " TEXT, "
                + Config.COLUMN_NOTES_NOTE + " TEXT, " //nullable
                + Config.COLUMN_NOTES_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP "
                + ")";

        db.execSQL(CREATE_NOTES_TABLE);
        db.execSQL(CREATE_SESSION_TABLE);
        db.execSQL(CREATE_SAFETY_TABLE);
        db.execSQL(CREATE_SAILING_TABLE);
        db.execSQL(CREATE_USER_TABLE);

        //Tag so i can find execution in my log code
        Log.d("***NIAMH_IS4447_DBH***","DB created!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_SAFETY);
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_SAILING);
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + Config.TABLE_SESSION);

        // Create tables again
        onCreate(db);
    }

    //opening the database
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        //enable foreign key constraints like ON UPDATE CASCADE, ON DELETE CASCADE
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

}
