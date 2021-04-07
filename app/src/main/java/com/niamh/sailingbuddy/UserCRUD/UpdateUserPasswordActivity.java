package com.niamh.sailingbuddy.UserCRUD;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.UpdateUser.UserUpdateListener2;

import java.io.IOException;
import java.util.List;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class UpdateUserPasswordActivity extends AppCompatActivity {

    /*
     * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
     */
    //Update Existing User
    String IdK = "ID_KEY";

    //Declaring Variables
    private static long userId;

    private User mUser;

    //Declaring Variables
    private EditText editTextPassword;
    private EditText editTextConfPassword;
    private TextView nameTextView;
    private TextView titleTextView;
    private LinearLayout layout_id;

    ImageView backImageView;
    ImageView updateImageView;

    String adminPassword;


    SharedPreferences sharedpreferences;
    private DatabaseQueryClass databaseQueryClass;

    String isLoggedIn = "LOGGED_IN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_password);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /*when the update button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
        nameTextView = findViewById(R.id.nameTextView);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfPassword = findViewById(R.id.editTextConfPassword);
        titleTextView = findViewById(R.id.titleTextView);
        layout_id = findViewById(R.id.layout_id);

        updateImageView = findViewById(R.id.createImageView);
        backImageView = findViewById(R.id.backImageView);

        databaseQueryClass = new DatabaseQueryClass(this);

        sharedpreferences = this.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        userId = sharedpreferences.getInt(IdK, 1);

        mUser = databaseQueryClass.getUserById(userId);
        String mobile = mUser.getMobNo();

        String nameUser = "Change password of account belonging to "+ mUser.getName() + " " + mUser.getEmailId();
        nameTextView.setText(nameUser);

        updateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateUserPasswordActivity.this);
                builder.setTitle("ID Check:");
                builder.setMessage("Please Enter Last 4 Digits of Your Phone Number to Confirm. You will be logged out Immediatly");

                // Set up the input
                final EditText input = new EditText(UpdateUserPasswordActivity.this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adminPassword = input.getText().toString();
                        if (adminPassword.length() == 4) {
                            if (mobile.contains(adminPassword)) {
                                Toast.makeText(UpdateUserPasswordActivity.this, "Password Correct", Toast.LENGTH_SHORT).show();
                                updateUser();

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putBoolean(isLoggedIn, false);
                                editor.commit();

                                Intent loginIntent = new Intent(UpdateUserPasswordActivity.this, LoginActivity.class);
                                startActivity(loginIntent);


                            } else {
                                Toast.makeText(UpdateUserPasswordActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                Intent profileIntent = new Intent(UpdateUserPasswordActivity.this, ProfileActivity.class);
                                startActivity(profileIntent);
                            }
                        } else {
                            Toast.makeText(UpdateUserPasswordActivity.this, "Password too Short", Toast.LENGTH_SHORT).show();
                            Intent profileIntent = new Intent(UpdateUserPasswordActivity.this, ProfileActivity.class);
                            startActivity(profileIntent);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editTextPassword.setText("");
                        editTextConfPassword.setText("");
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });


//if the cancel button is pressed we return to the view
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(UpdateUserPasswordActivity.this, ProfileActivity.class);
                startActivity(backIntent);
            }
        });


    }


    public void updateUser() {
        if (!editTextPassword.getText().toString().isEmpty() &&
                !editTextConfPassword.getText().toString().isEmpty()
                ) {

            String password = editTextPassword.getText().toString();
            String confPass = editTextConfPassword.getText().toString();

            if (password.equals(confPass)) {
                mUser.setPassword(password);


                // Shared preferences insporation from https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
                int i = mUser.getID();

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(IdK, i);

                editor.apply();

                long id = databaseQueryClass.updateUser(mUser);
            } else {
                    Toast.makeText(UpdateUserPasswordActivity.this, "Password is not matching", Toast.LENGTH_SHORT).show();
                //layout_id.setAnimation(shakeAnimation);
                    editTextPassword.setText("");
                    editTextConfPassword.setText("");
                }
        }
    }

}