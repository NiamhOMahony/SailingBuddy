package com.niamh.sailingbuddy.UserCRUD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.UpdateUser.UserUpdateDialogFragment;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class ProfileActivity extends AppCompatActivity {

    private TextView firstName;
    private TextView lastName;
    private TextView emailId;
    private TextView mobileNumber;
    private TextView typeSpinner;

    private Button editProfileButton;
    private Button editPasswordButton;

    private ImageView profileImageView;

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    DatabaseQueryClass databaseQueryClass;
    SharedPreferences sharedpreferences;

    String IdK = "ID_KEY";
    User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        databaseQueryClass = new DatabaseQueryClass(this);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        firstName = findViewById(R.id.firstName);
        emailId = findViewById(R.id.userEmailId);
        mobileNumber = findViewById(R.id.mobileNumber);
        typeSpinner = findViewById(R.id.typeUser);
        profileImageView = findViewById(R.id.profileImageView);

        editProfileButton = findViewById(R.id.EditBtn);
        editPasswordButton = findViewById(R.id.passwordBtn);

        int ID = sharedpreferences.getInt(IdK, 1);
        mUser = databaseQueryClass.getUserById(ID);

        firstName.setText(mUser.getName());
        mobileNumber.setText(mUser.getMobNo());
        emailId.setText(mUser.getEmailId());
        typeSpinner.setText(mUser.getType());
        profileImageView.setImageBitmap(mUser.getImage());


        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(ProfileActivity.this, UpdateUserActivity.class);
                startActivity(editIntent);
            }
        });

        editPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordIntent = new Intent(ProfileActivity.this, UpdateUserPasswordActivity.class);
                startActivity(passwordIntent);
            }
        });

         /*Code adapted from How to Implement Bottom Navigation With Activities in Android Studio
         | BottomNav | Android Coding https://www.youtube.com/watch?v=JjfSjMs0ImQ*/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.menuUser);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menuSettings:
                    Intent SettingsIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                    startActivity(SettingsIntent);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHome:
                    Intent dashboardIntent = new Intent(ProfileActivity.this, DashboardActivity.class);
                    startActivity(dashboardIntent);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuUser:

                    return true;
            }
            return false;
        });

    }

}