package com.niamh.sailingbuddy.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.niamh.sailingbuddy.NoteCRUD.CreateNote.Notes;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.Safety;
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;
import com.niamh.sailingbuddy.Utils.Config;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.niamh.sailingbuddy.Utils.Config.TABLE_USER;

public class DatabaseQueryClass {

    private final Context context;

    private ByteArrayOutputStream objectByteArrayOutputStream;
    private byte[] imageInBytes;

    public DatabaseQueryClass(Context context){
        this.context = context;
    }

    //Inserting into the safety table
    public long insertSafety(Safety safety){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Bitmap imageToStoreBitmap = safety.getImage();
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,50,objectByteArrayOutputStream);
        imageInBytes = objectByteArrayOutputStream.toByteArray();

        contentValues.put(Config.COLUMN_SAFETY_TYPE, safety.getType());
        contentValues.put(Config.COLUMN_SAFETY_DESCRIPTION, safety.getDescription());
        contentValues.put(Config.COLUMN_SAFETY_AVAILABLE, safety.getAvailable());
        contentValues.put(Config.COLUMN_SAFETY_AVAILUSER, safety.getAvailuser());
        contentValues.put(Config.COLUMN_SAFETY_FAULT, safety.getFault());
        contentValues.put(Config.COLUMN_SAFETY_FAULTUSER, safety.getFaultuser());
        contentValues.put(Config.COLUMN_SAFETY_FAULTDES, safety.getFaultdes());
        contentValues.put(Config.COLUMN_SAFETY_IMAGE, imageInBytes);

        //Try catch statement for if the contet is blank and fails a message should post
        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_SAFETY, null, contentValues);
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP***", "Insert Exception: "+ e.getMessage());
            Toast.makeText(context, "Insert Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return id;
    }
    //Getting all the values of the Safety equipment table
    public List<Safety> getAllSafety(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        List<Safety> safetyList = new ArrayList<>();
        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
             Cursor cursor = sqLiteDatabase.query(Config.TABLE_SAFETY, null, null, null,
                     null, null, null, null)) {
            if (cursor.getCount()!=0){
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_SAFETY_ID));
                    String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_TYPE));
                    String description = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_DESCRIPTION));
                    String available = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_AVAILABLE));
                    String availuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_AVAILUSER));
                    String fault = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_FAULT));
                    String faultuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_FAULTUSER));
                    String faultdes = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_FAULTDES));
                    byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Config.COLUMN_SAFETY_IMAGE));


                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    safetyList.add(new Safety(id, type, description, available, availuser, fault, faultuser, faultdes, bitmap));

                }

                return safetyList;
            }
            //Try catch statement for if the contet is blank and fails a message should post
        } catch (Exception e) {
            Log.d("***NIAMH_FYP***", "Exception: " + e.getMessage());
            Toast.makeText(context, "Get All Operation failed", Toast.LENGTH_SHORT).show();
        }

        return Collections.emptyList();
    }
    //Getting each attribute by its ID number
    public Safety getSafetyById(long safetyId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Safety safety = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.query(Config.TABLE_SAFETY, null,
                    Config.COLUMN_SAFETY_ID + " = ? ", new String[] {String.valueOf(safetyId)},
                    null, null, null);

            if(cursor!=null && cursor.getCount() > 0)
            {
                if (cursor.moveToFirst())
                {
                    do {
                        String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_TYPE));
                        String description = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_DESCRIPTION));
                        String available = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_AVAILABLE));
                        String availuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_AVAILUSER));
                        String fault = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_FAULT));
                        String faultuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_FAULTUSER));
                        String faultdes = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAFETY_FAULTDES));
                        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Config.COLUMN_SAFETY_IMAGE));

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        safety = new Safety((int) safetyId, type, description, available, availuser, fault, faultuser, faultdes, bitmap);
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Get Safety By ID Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return safety;
    }
    //Updating the info in the safety equipment table
    public long updateSafety(Safety safety){

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Bitmap imageToStoreBitmap = safety.getImage();
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,50,objectByteArrayOutputStream);
        imageInBytes = objectByteArrayOutputStream.toByteArray();

        contentValues.put(Config.COLUMN_SAFETY_TYPE, safety.getType());
        contentValues.put(Config.COLUMN_SAFETY_DESCRIPTION, safety.getDescription());
        contentValues.put(Config.COLUMN_SAFETY_AVAILABLE, safety.getAvailable());
        contentValues.put(Config.COLUMN_SAFETY_AVAILUSER, safety.getAvailuser());
        contentValues.put(Config.COLUMN_SAFETY_FAULT, safety.getFault());
        contentValues.put(Config.COLUMN_SAFETY_FAULTUSER, safety.getFaultuser());
        contentValues.put(Config.COLUMN_SAFETY_FAULTDES, safety.getFaultdes());
        contentValues.put(Config.COLUMN_SAFETY_IMAGE, imageInBytes);

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_SAFETY, contentValues,
                    Config.COLUMN_SAFETY_ID + " = ? ",
                    new String[] {String.valueOf(safety.getId())});
        } catch (SQLiteException e){
            Log.d("NIAMH_FYP", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }
    //Delete each safety attribute individulaly by its id
    public boolean deleteSafetyById(long safetyId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        int row = sqLiteDatabase.delete(Config.TABLE_SAFETY,
                Config.COLUMN_SAFETY_ID + " = ? ", new String[]{String.valueOf(safetyId)});

        return row > 0;
    }
    //Delete everything in safety at once
    public boolean deleteAllSafety(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
            sqLiteDatabase.delete(Config.TABLE_SAFETY, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_SAFETY);

            if (count == 0)
                deleteStatus = true;

        } catch (SQLiteException e) {
            Log.d("***NIAMH_FYP***", "Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return deleteStatus;
    }
    //Is not currently used in application but can be used for reporting in th future this counts how much safety equipment there is
    public long getNumberOfSafety(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_SAFETY);
        } catch (SQLiteException e) {
            Log.d("NIAMH_FYP", "Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return count;
    }

    //Inserting into the sailing table
    public long insertSailing(Sailing sailing){

        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Bitmap imageToStoreBitmap = sailing.getImage();
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,50,objectByteArrayOutputStream);
        imageInBytes = objectByteArrayOutputStream.toByteArray();

        contentValues.put(Config.COLUMN_SAILING_TYPE, sailing.getType());
        contentValues.put(Config.COLUMN_SAILING_DESCRIPTION, sailing.getDescription());
        contentValues.put(Config.COLUMN_SAILING_AVAILABLE, sailing.getAvailable());
        contentValues.put(Config.COLUMN_SAILING_AVAILUSER, sailing.getAvailuser());
        contentValues.put(Config.COLUMN_SAILING_FAULT, sailing.getFault());
        contentValues.put(Config.COLUMN_SAILING_FAULTUSER, sailing.getFaultuser());
        contentValues.put(Config.COLUMN_SAILING_FAULTDES, sailing.getFaultdes());
        contentValues.put(Config.COLUMN_SAILING_IMAGE, imageInBytes);

        //Try catch statement for if the content is blank and fails a message should post
        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_SAILING, null, contentValues);
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP***", "Insert Exception: "+ e.getMessage());
            Toast.makeText(context, "Insert Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return id;
    }
    //Getting all the values of the Sailing equipment table
    public List<Sailing> getAllSailing(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        List<Sailing> sailingList = new ArrayList<>();
        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
             Cursor cursor = sqLiteDatabase.query(Config.TABLE_SAILING, null, null, null,
                     null, null, null, null)) {
            if (cursor.getCount()!=0){
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_SAILING_ID));
                    String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_TYPE));
                    String description = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_DESCRIPTION));
                    String available = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_AVAILABLE));
                    String availuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_AVAILUSER));
                    String fault = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_FAULT));
                    String faultuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_FAULTUSER));
                    String faultdes = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_FAULTDES));
                    byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Config.COLUMN_SAILING_IMAGE));

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    sailingList.add(new Sailing(id, type, description, available, availuser, fault, faultuser, faultdes, bitmap));

                }

                return sailingList;
            }
            //Try catch statement for if the contet is blank and fails a message should post
        } catch (Exception e) {
            Log.d("***NIAMH_FYP***", "Exception: " + e.getMessage());
            Toast.makeText(context, "Get All Operation failed", Toast.LENGTH_SHORT).show();
        }

        return Collections.emptyList();
    }
    //Getting each attribute by its ID number
    public Sailing getSailingById(long sailingId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Sailing sailing = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.query(Config.TABLE_SAILING, null,
                    Config.COLUMN_SAILING_ID + " = ? ", new String[] {String.valueOf(sailingId)},
                    null, null, null);

            if(cursor!=null && cursor.getCount() > 0)
            {
                if (cursor.moveToFirst())
                {
                    do {
                        String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_TYPE));
                        String description = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_DESCRIPTION));
                        String available = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_AVAILABLE));
                        String availuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_AVAILUSER));
                        String fault = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_FAULT));
                        String faultuser = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_FAULTUSER));
                        String faultdes = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SAILING_FAULTDES));
                        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Config.COLUMN_SAILING_IMAGE));

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        sailing = new Sailing((int) sailingId, type, description, available, availuser, fault, faultuser, faultdes, bitmap);
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Get Sailing By ID Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return sailing;
    }
    //Updating the info in the sailing equipment table
    public long updateSailing(Sailing sailing){

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Bitmap imageToStoreBitmap = sailing.getImage();
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,50,objectByteArrayOutputStream);
        imageInBytes = objectByteArrayOutputStream.toByteArray();

        contentValues.put(Config.COLUMN_SAILING_TYPE, sailing.getType());
        contentValues.put(Config.COLUMN_SAILING_DESCRIPTION, sailing.getDescription());
        contentValues.put(Config.COLUMN_SAILING_AVAILABLE, sailing.getAvailable());
        contentValues.put(Config.COLUMN_SAILING_AVAILUSER, sailing.getAvailuser());
        contentValues.put(Config.COLUMN_SAILING_FAULT, sailing.getFault());
        contentValues.put(Config.COLUMN_SAILING_FAULTUSER, sailing.getFaultuser());
        contentValues.put(Config.COLUMN_SAILING_FAULTDES, sailing.getFaultdes());
        contentValues.put(Config.COLUMN_SAILING_IMAGE, imageInBytes);

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_SAILING, contentValues,
                    Config.COLUMN_SAILING_ID + " = ? ",
                    new String[] {String.valueOf(sailing.getId())});
        } catch (SQLiteException e){
            Log.d("NIAMH_FYP", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }
    //Delete each sailing attribute individulaly by its id
    public boolean deleteSailingById(long sailingId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        int row = sqLiteDatabase.delete(Config.TABLE_SAILING,
                Config.COLUMN_SAILING_ID + " = ? ", new String[]{String.valueOf(sailingId)});

        return row > 0;
    }
    //Delete everything in sailing at once
    public boolean deleteAllSailing(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
            sqLiteDatabase.delete(Config.TABLE_SAILING, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_SAILING);

            if (count == 0)
                deleteStatus = true;

        } catch (SQLiteException e) {
            Log.d("***NIAMH_FYP***", "Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return deleteStatus;
    }
    //Is not currently used in application but can be used for reporting in th future this counts how much sailing equipment there is
    public long getNumberOfSailing(){
        long count = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
            count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_SAILING);
        } catch (SQLiteException e) {
            Log.d("NIAMH_FYP", "Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return count;
    }

    //User Qeries
    public long insertUser(User user){
    // code to add the new register
        long id = -1;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        Bitmap imageToStoreBitmap = user.getImage();
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,50,objectByteArrayOutputStream);
        imageInBytes = objectByteArrayOutputStream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(Config.COLUMN_USER_NAME, user.getName()); // register  Name
        values.put(Config.COLUMN_USER_EMAIL, user.getEmailId());//register email id
        values.put(Config.COLUMN_USER_MOBILE, user.getMobNo());//register mobile no
        values.put(Config.COLUMN_USER_TYPE, user.getType());
        values.put(Config.COLUMN_USER_PASSSWORD, user.getPassword());
        values.put(Config.COLUMN_USER_IMAGE, imageInBytes);

        //Try catch statement for if the content is blank and fails a message should post
        try {
            id = sqLiteDatabase.insertOrThrow(TABLE_USER, null, values);
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP***", "Insert Exception: "+ e.getMessage());
            Toast.makeText(context, "Insert Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return id;

    }

    public long updateUser(User user) {

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        Bitmap imageToStoreBitmap = user.getImage();
        objectByteArrayOutputStream = new ByteArrayOutputStream();
        imageToStoreBitmap.compress(Bitmap.CompressFormat.JPEG,50,objectByteArrayOutputStream);
        imageInBytes = objectByteArrayOutputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_USER_NAME,user.getName()); // register Name
        contentValues.put(Config.COLUMN_USER_EMAIL, user.getEmailId());//register email id
        contentValues.put(Config.COLUMN_USER_MOBILE, user.getMobNo());//register mobile no
        contentValues.put(Config.COLUMN_USER_TYPE, user.getType());
        contentValues.put(Config.COLUMN_USER_PASSSWORD, user.getPassword());
        contentValues.put(Config.COLUMN_USER_IMAGE, imageInBytes);

        try {
            rowCount = sqLiteDatabase.update(TABLE_USER, contentValues,
                    Config.COLUMN_USER_ID + " = ? ",
                    new String[] {String.valueOf(user.getID())});
        } catch (SQLiteException e){
            Log.d("NIAMH_FYP", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    public long getUsersCount() {
            long count = -1;
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

            try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
                count = DatabaseUtils.queryNumEntries(sqLiteDatabase, TABLE_USER);
            } catch (SQLiteException e) {
                Log.d("NIAMH_FYP", "Exception: " + e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            return count;
    }

    public Boolean checkUser(String email, String password) {
        String[] columns = {Config.COLUMN_USER_ID};
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String selection = Config.COLUMN_USER_EMAIL + "=?" + " and " + Config.COLUMN_USER_PASSSWORD + "=?";
        String[] selectionArgs = {email, password};
        Cursor cursor = sqLiteDatabase.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        sqLiteDatabase.close();

        return count > 0;
    }

    public int getUserId(@NotNull String email, @NotNull String password) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USER
                + " WHERE " + Config.COLUMN_USER_EMAIL + " = '"+ email.trim()
                +"'" +" and "+ Config.COLUMN_USER_PASSSWORD + " = '"+password.trim()
                +"'" , null);
        cursor.moveToFirst();

        int id = 0 ;
        String username = "";
        while (cursor != null) {
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_ID)));
            username = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_NAME));
            Log.d("tagOneUser", Integer.toString(id) );
            Log.d("tagOneUser", username );
            break;
        }
        cursor.moveToNext();
        return id;
    }

    public String getUserType(@NotNull String email, @NotNull String password) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_USER
                + " WHERE " + Config.COLUMN_USER_EMAIL + " = '"+ email.trim()
                +"'" +" and "+ Config.COLUMN_USER_PASSSWORD + " = '"+password.trim()
                +"'" , null);
        cursor.moveToFirst();

        int id = 0 ;
        String type = "";
        while (cursor != null) {
            id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_ID)));
            type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_TYPE));
            Log.d("tagOneUser", Integer.toString(id) );
            Log.d("tagOneUser", type );
            break;
        }
        cursor.moveToNext();
        return type;
    }

    public User getUserById(long userId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        User user = null;
        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.query(TABLE_USER, null,
                    Config.COLUMN_USER_ID + " = ? ", new String[] {String.valueOf(userId)},
                    null, null, null);

            if(cursor!=null && cursor.getCount() > 0)
            {
                if (cursor.moveToFirst())
                {
                    do {
                        String fName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_NAME));
                        String email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));
                        String mobile = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_MOBILE));
                        String password = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_PASSSWORD));
                        String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_TYPE));
                        byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Config.COLUMN_USER_IMAGE));

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        user = new User((int) userId, fName, email, mobile, password, type, bitmap);
                    } while (cursor.moveToNext());
                }
            }
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Get Sailing By ID Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }
        return user;
    }

    //Delete each sailing attribute individulaly by its id
    public boolean deleteUserById(long userId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        int row = sqLiteDatabase.delete(TABLE_USER,
                Config.COLUMN_USER_ID + " = ? ", new String[]{String.valueOf(userId)});

        return row > 0;
    }

    //Delete everything in sailing at once
    public boolean deleteAllUser(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);

        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase()) {
            sqLiteDatabase.delete(TABLE_USER, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, TABLE_USER);

            if (count == 0)
                deleteStatus = true;

        } catch (SQLiteException e) {
            Log.d("***NIAMH_FYP***", "Exception: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return deleteStatus;
    }

    //Getting all the values of the Sailing equipment table
    public List<User> getAllUsers(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        List<User> userList = new ArrayList<>();
        try (SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
             Cursor cursor = sqLiteDatabase.query(TABLE_USER, null, null, null,
                     null, null, null, null)) {
            if (cursor.getCount()!=0){
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_ID));
                    String fName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_NAME));
                    String email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));
                    String mobile = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_MOBILE));
                    String password = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_PASSSWORD));
                    String type = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_TYPE));
                    byte[] imageBytes = cursor.getBlob(cursor.getColumnIndex(Config.COLUMN_USER_IMAGE));

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                    userList.add(new User(id, fName, email, mobile, password, type, bitmap));

                }

                return userList;
            }
            //Try catch statement for if the contet is blank and fails a message should post
        } catch (Exception e) {
            Log.d("***NIAMH_FYP***", "Exception: " + e.getMessage());
            Toast.makeText(context, "Get All Operation failed", Toast.LENGTH_SHORT).show();
        }

        return Collections.emptyList();
    }

    //Inserting into the Session table
    public long insertSession(Session session){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        long id =  -1;;

        contentValues.put(Config.COLUMN_SESSION_INSTRUCTOR, session.getInstructorName());
        contentValues.put(Config.COLUMN_SESSION_DATE, session.getDate());
        contentValues.put(Config.COLUMN_SESSION_LEVEL, session.getLevel());
        contentValues.put(Config.COLUMN_SESSION_STUDENTS, session.getNoStudents());
        contentValues.put(Config.COLUMN_SESSION_LAUNCH, session.getLaunchTime());
        contentValues.put(Config.COLUMN_SESSION_RECOVERY, session.getRecoveryTime());
        contentValues.put(Config.COLUMN_SESSION_LAND, session.getLandActvity());
        contentValues.put(Config.COLUMN_SESSION_WATER, session.getWaterActivity());
        contentValues.put(Config.COLUMN_SESSION_AREA, session.getSailArea());
        contentValues.put(Config.COLUMN_SESSION_HIGH, session.getHighTide());
        contentValues.put(Config.COLUMN_SESSION_LOW, session.getLowTide());
        contentValues.put(Config.COLUMN_SESSION_WEATHER, session.getWeather());


        //Try catch statement for if the contet is blank and fails a message should post
        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_SESSION, null, contentValues);
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP_DBQ1***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    //Getting all the values of the Sessions table
    public List<Session> getAllSession(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_SESSION, null, null, null, null, null, null, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Session> sessionList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_SESSION_ID));
                        String instructorName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_INSTRUCTOR));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_DATE));
                        String level = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LEVEL));
                        String noStudents = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_STUDENTS));
                        String launchTime = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LAUNCH));
                        String recoveryTime = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_RECOVERY));
                        String landActivity = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LAND));
                        String waterActivity = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_WATER));
                        String sailArea = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_AREA));
                        String highTide = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_HIGH));
                        String lowTide = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LOW));
                        String weather = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_WEATHER));

                        sessionList.add(new Session(id, instructorName, date, level, noStudents, launchTime, recoveryTime,
                                landActivity, waterActivity, sailArea, highTide, lowTide, weather));
                    }   while (cursor.moveToNext());

                    return sessionList;
                }
            //Try catch statement for if the contet is blank and fails a message should post
        } catch (Exception e){
            Log.d("***NIAMH_FYP_DBQ2***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    //Grtting each attribute by its ID number
    public Session getSessionById(long sessionId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Session session = null;

        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.query(Config.TABLE_SESSION, null,
                    Config.COLUMN_SESSION_ID + " = ? ", new String[] {String.valueOf(sessionId)},
                    null, null, null);

            if(cursor!=null && cursor.moveToFirst()){
                String instructorName = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_INSTRUCTOR));
                String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_DATE));
                String level = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LEVEL));
                String noStudents = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_STUDENTS));
                String launchTime = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LAUNCH));
                String recoveryTime = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_RECOVERY));
                String landActivity = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LAND));
                String waterActivity = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_WATER));
                String sailArea = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_AREA));
                String highTide = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_HIGH));
                String lowTide = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_LOW));
                String weather = cursor.getString(cursor.getColumnIndex(Config.COLUMN_SESSION_WEATHER));

                session = new Session(sessionId, instructorName, date, level, noStudents, launchTime, recoveryTime,
                        landActivity, waterActivity, sailArea, highTide, lowTide, weather);

            }
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return session;
    }

    //Updating the info in the Sessions table
    public long updateSession(Session session){

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_SESSION_INSTRUCTOR, session.getInstructorName());
        contentValues.put(Config.COLUMN_SESSION_DATE, session.getDate());
        contentValues.put(Config.COLUMN_SESSION_LEVEL, session.getLevel());
        contentValues.put(Config.COLUMN_SESSION_STUDENTS, session.getNoStudents());
        contentValues.put(Config.COLUMN_SESSION_LAUNCH, session.getLaunchTime());
        contentValues.put(Config.COLUMN_SESSION_RECOVERY, session.getRecoveryTime());
        contentValues.put(Config.COLUMN_SESSION_LAND, session.getLandActvity());
        contentValues.put(Config.COLUMN_SESSION_WATER, session.getWaterActivity());
        contentValues.put(Config.COLUMN_SESSION_AREA, session.getSailArea());
        contentValues.put(Config.COLUMN_SESSION_HIGH, session.getHighTide());
        contentValues.put(Config.COLUMN_SESSION_LOW, session.getLowTide());
        contentValues.put(Config.COLUMN_SESSION_WEATHER, session.getWeather());

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_SESSION, contentValues,
                    Config.COLUMN_SESSION_ID + " = ? ",
                    new String[] {String.valueOf(session.getId())});
        } catch (SQLiteException e){
            Log.d("NIAMH_DBQ_4", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    //Delete each Session attribute individulaly by its id
    public boolean deleteSessionById(long sessionId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        int row = sqLiteDatabase.delete(Config.TABLE_SESSION,
                Config.COLUMN_SESSION_ID + " = ? ", new String[]{String.valueOf(sessionId)});

        return row > 0;
    }

    //Delete everything in Session at once
    public boolean deleteAllSessions(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            sqLiteDatabase.delete(Config.TABLE_SESSION, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_SESSION);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Log.d("***NIAMH_IS4447_DBQ3***", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return deleteStatus;
    }

    //Inserting into the notes table
    public long insertNote(Notes notes){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        long id =  -1;;

        contentValues.put(Config.COLUMN_NOTES_TITLE, notes.getTitle());
        contentValues.put(Config.COLUMN_NOTES_SUBTITLE, notes.getSubtitle());
        contentValues.put(Config.COLUMN_NOTES_NOTE, notes.getNote());
        contentValues.put(Config.COLUMN_NOTES_DATE, notes.getDateTime());

        //Try catch statement for if the contet is blank and fails a message should post
        try {
            id = sqLiteDatabase.insertOrThrow(Config.TABLE_NOTES, null, contentValues);
        } catch (SQLiteException e){
            Log.d("***NIAMH_FYP_DBQ1***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }

    //Getting all the values of the Notes table
    public List<Notes> getAllNotes(){

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {

            cursor = sqLiteDatabase.query(Config.TABLE_NOTES, null, null, null, null, null, null, null);

            if(cursor!=null)
                if(cursor.moveToFirst()){
                    List<Notes> notesList = new ArrayList<>();
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_NOTES_ID));
                        String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_TITLE));
                        String subtitle = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_SUBTITLE));
                        String note = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_NOTE));
                        String datTime = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_DATE));

                        notesList.add(new Notes(id, title, subtitle, note, datTime ));
                    }   while (cursor.moveToNext());

                    return notesList;
                }
            //Try catch statement for if the contet is blank and fails a message should post
        } catch (Exception e){
            Log.d("***NIAMH_FYP_DBQ2***", "Exception: "+ e.getMessage());
            Toast.makeText(context, "Operation failed", Toast.LENGTH_SHORT).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return Collections.emptyList();
    }

    //Grtting each attribute by its ID number
    public Notes getNoteById(long notesId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Notes notes = null;

        Cursor cursor = null;
        try{
            cursor = sqLiteDatabase.query(Config.TABLE_NOTES, null,
                    Config.COLUMN_NOTES_ID + " = ? ", new String[] {String.valueOf(notesId)},
                    null, null, null);

            if(cursor!=null && cursor.moveToFirst()){
                String title = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_TITLE));
                String subtitle = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_SUBTITLE));
                String note = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_NOTE));
                String dateTime = cursor.getString(cursor.getColumnIndex(Config.COLUMN_NOTES_DATE));

                notes = new Notes((int) notesId, title, subtitle, note, dateTime);
            }
        } catch (SQLiteException e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if(cursor!=null)
                cursor.close();
            sqLiteDatabase.close();
        }

        return notes;
    }

    //Updating the info in the notes table
    public long updateNote(Notes notes){

        long rowCount = 0;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_NOTES_TITLE, notes.getTitle());
        contentValues.put(Config.COLUMN_NOTES_SUBTITLE, notes.getSubtitle());
        contentValues.put(Config.COLUMN_NOTES_NOTE, notes.getNote());
        contentValues.put(Config.COLUMN_NOTES_DATE, notes.getDateTime());

        try {
            rowCount = sqLiteDatabase.update(Config.TABLE_NOTES, contentValues,
                    Config.COLUMN_NOTES_ID + " = ? ",
                    new String[] {String.valueOf(notes.getId())});
        } catch (SQLiteException e){
            Log.d("NIAMH_DBQ_4", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return rowCount;
    }

    //Delete each note attribute individulaly by its id
    public boolean deleteNoteById(long notesId) {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        int row = sqLiteDatabase.delete(Config.TABLE_NOTES,
                Config.COLUMN_NOTES_ID + " = ? ", new String[]{String.valueOf(notesId)});

        return row > 0;
    }

    //Delete everything in note at once
    public boolean deleteAllNotes(){
        boolean deleteStatus = false;
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            sqLiteDatabase.delete(Config.TABLE_NOTES, null, null);

            long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, Config.TABLE_NOTES);

            if(count==0)
                deleteStatus = true;

        } catch (SQLiteException e){
            Log.d("***NIAMH_IS4447_DBQ3***", "Exception: "+ e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }
        return deleteStatus;
    }




}
