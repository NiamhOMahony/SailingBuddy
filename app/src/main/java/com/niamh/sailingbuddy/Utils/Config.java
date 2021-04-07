package com.niamh.sailingbuddy.Utils;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

public class Config {

    public static final String DATABASE_NAME = "sailing_buddy_data";

    //Column names of safety table
    public static final String TABLE_SAFETY= "safety_table";
    public static final String COLUMN_SAFETY_ID = "_id";
    public static final String COLUMN_SAFETY_TYPE = "type";
    public static final String COLUMN_SAFETY_DESCRIPTION = "description";
    public static final String COLUMN_SAFETY_AVAILABLE = "available";
    public static final String COLUMN_SAFETY_AVAILUSER = "availuser";
    public static final String COLUMN_SAFETY_FAULT = "fault";
    public static final String COLUMN_SAFETY_FAULTUSER = "faultuser";
    public static final String COLUMN_SAFETY_FAULTDES = "faultdes";
    public static final String COLUMN_SAFETY_IMAGE = "image";

    //Column names of sailing table
    public static final String TABLE_SAILING= "sailing_table";
    public static final String COLUMN_SAILING_ID = "_id";
    public static final String COLUMN_SAILING_TYPE = "type";
    public static final String COLUMN_SAILING_DESCRIPTION = "description";
    public static final String COLUMN_SAILING_AVAILABLE = "available";
    public static final String COLUMN_SAILING_AVAILUSER = "availuser";
    public static final String COLUMN_SAILING_FAULT = "fault";
    public static final String COLUMN_SAILING_FAULTUSER = "faultuser";
    public static final String COLUMN_SAILING_FAULTDES = "faultdes";
    public static final String COLUMN_SAILING_IMAGE = "image";

    //Column names of users table
    public static final String TABLE_USER= "user_table";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_TYPE = "type";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_MOBILE = "phone";
    public static final String COLUMN_USER_PASSSWORD = "password";
    public static final String COLUMN_USER_IMAGE = "image";

    //column names of session table
    public static final String TABLE_SESSION= "session";
    public static final String COLUMN_SESSION_ID = "_id";
    public static final String COLUMN_SESSION_INSTRUCTOR = "instructor";
    public static final String COLUMN_SESSION_DATE = "date";
    public static final String COLUMN_SESSION_LEVEL = "level";
    public static final String COLUMN_SESSION_STUDENTS = "students";
    public static final String COLUMN_SESSION_LAUNCH = "launch";
    public static final String COLUMN_SESSION_RECOVERY = "recovery";
    public static final String COLUMN_SESSION_LAND = "land";
    public static final String COLUMN_SESSION_WATER = "water";
    public static final String COLUMN_SESSION_AREA = "area";
    public static final String COLUMN_SESSION_HIGH = "highTIde";
    public static final String COLUMN_SESSION_LOW = "lowTide";
    public static final String COLUMN_SESSION_WEATHER = "weather";

    //column names of notes table
    public static final String TABLE_NOTES= "notes";
    public static final String COLUMN_NOTES_ID = "_id";
    public static final String COLUMN_NOTES_DATE = "dateTime";
    public static final String COLUMN_NOTES_TITLE = "title";
    public static final String COLUMN_NOTES_SUBTITLE = "subtitle";
    public static final String COLUMN_NOTES_NOTE = "note";

    //others for general purpose key-value pair data
    public static final String TITLE = "sailing_buddy";
    public static final String CREATE_SAFETY= "create_safety";
    public static final String UPDATE_SAFETY = "update_safety";
    public static final String CREATE_SAILING= "create_sailing";
    public static final String UPDATE_SAILING = "update_sailing";
    public static final String UPDATE_USER= "update_user";
    public static final String CREATE_USER = "create_user";
    public static final String CREATE_NOTES= "create_notes";
    public static final String UPDATE_NOTES = "update_notes";
    public static final String CREATE_SESSION= "create_session";
    public static final String UPDATE_SESSION = "update_session";

}

