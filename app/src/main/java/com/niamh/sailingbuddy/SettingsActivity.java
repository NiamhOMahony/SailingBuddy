package com.niamh.sailingbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niamh.sailingbuddy.UserCRUD.LoginActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat darkModeSwitch;
    TextView Logout;
    Long ID ;
    SharedPreferences sharedpreferences2;
    String isLoggedIn = "LOGGED_IN";
    String typeK = "TYPE_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        /* Code adapted from How to Save State Of Switch Button in Android Studio
         | SaveSwitchState | Android Coding https://www.youtube.com/watch?v=RyiTx8lWdx0*/

        /*Changing state code adapted from Android Studio - Night/Dark Mode For Your App - Tutorial
         https://www.youtube.com/watch?v=QhGf8fGJM8U*/

        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        Logout = findViewById(R.id.logoutTextView);

        sharedpreferences2 = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String type = sharedpreferences2.getString(typeK,"");

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);


        darkModeSwitch.setChecked(sharedPreferences.getBoolean("value", true));

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));

                SharedPreferences.Editor editor = sharedpreferences2.edit();
                editor.putBoolean(isLoggedIn, false);
                editor.apply();
            }
        });
        darkModeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (darkModeSwitch.isChecked()){
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", true);
                    editor.apply();
                    darkModeSwitch.setChecked(true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else {
                    SharedPreferences.Editor editor = getSharedPreferences("save", MODE_PRIVATE).edit();
                    editor.putBoolean("value", false);
                    editor.apply();
                    darkModeSwitch.setChecked(false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        /*Code adapted from How to Implement Bottom Navigation With Activities in Android Studio
         | BottomNav | Android Coding https://www.youtube.com/watch?v=JjfSjMs0ImQ*/

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);

        bottomNavigationView.setSelectedItemId(R.id.menuSettings);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuSettings:
                        return true;
                    case R.id.menuHome:
                           Intent dashboardIntent = new Intent(SettingsActivity.this, DashboardActivity.class);
                            startActivity(dashboardIntent);
                            overridePendingTransition(0,0);
                            return true;

                    case R.id.menuUser:

                        Intent profileIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

    }
}
