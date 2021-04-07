package com.niamh.sailingbuddy.UserCRUD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;

public class LoginActivity extends AppCompatActivity {

    DatabaseQueryClass databaseQueryClass;
    Cursor cursor;

    private  ImageView logo;
    private TextView  logoText;
    private  EditText emailid, pass;
    private Button loginButton;
    private TextView forgotPassword, signUp;
    private  CheckBox show_hide_password;
    private  LinearLayout loginLayout;
    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    String nameKey = "NAME_KEY";
    String typeK = "TYPE_KEY";
    String emailK = "EMAIL_KEY";
    String passwordK = "PASSWORD_KEY";
    String mobile = "MOBILE_KEY";
    String isLoggedIn = "LOGGED_IN";
    String IdK = "ID_KEY";

    User mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseQueryClass = new DatabaseQueryClass(this);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Boolean loggedIn = sharedpreferences.getBoolean("LOGGED_IN", false);

        if(loggedIn){
            Intent profileIntent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(profileIntent);
            return;
        }
        setContentView(R.layout.activity_login);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        emailid = findViewById(R.id.login_emailid);
        pass = findViewById(R.id.login_password);
        logo = findViewById(R.id.logo);
        logoText = findViewById(R.id.logoText);
        signUp = findViewById(R.id.createAccount);
        show_hide_password = findViewById(R.id.show_hide_password);
        loginButton = findViewById(R.id.loginBtn);

        loginLayout =  findViewById(R.id.login_layout);

        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(this, R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(this, R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(this, R.anim.anim_from_left);
        anim_from_right = AnimationUtils.loadAnimation(this, R.anim.anim_from_right);
        anim_to_left = AnimationUtils.loadAnimation(this, R.anim.left_out);
        anim_to_right = AnimationUtils.loadAnimation(this, R.anim.right_out);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        //Animate OnCreate
        logo.setAnimation(anim_from_top);
        logoText.setAnimation(anim_from_top);
        emailid.setAnimation(anim_from_left);
        pass.setAnimation(anim_from_left);
        show_hide_password.setAnimation(anim_from_left);
        loginButton.setAnimation(anim_from_button);
        signUp.setAnimation(anim_from_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailid.getText().toString().trim();
                String password = pass.getText().toString().trim();


                Boolean res = databaseQueryClass.checkUser(email, password);


                if (res == true) {
                    Intent Dashboard = new Intent(LoginActivity.this, DashboardActivity.class);
                    Bundle b = new Bundle();
                    b.putString("textViewEmail", emailid.getText().toString());
                    b.putString("textViewPassword", pass.getText().toString());

                    int id = databaseQueryClass.getUserId(email,password);
                    String type = databaseQueryClass.getUserType(email, password);
                    String userType = "Instructor";

                    mUser = databaseQueryClass.getUserById(id);

                    // Shared preferences insporation from https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
                    String n = mUser.getName();
                    String e = mUser.getEmailId();
                    String p = mUser.getPassword();
                    String t = mUser.getType();
                    String m = mUser.getMobNo();
                    int i = mUser.getID();

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(nameKey, n);
                    editor.putString(emailK, e);
                    editor.putString(passwordK, p);
                    editor.putString(typeK, t);
                    editor.putString(mobile, m);
                    editor.putInt(IdK, i);
                    editor.putBoolean(isLoggedIn, true);

                    editor.commit();

                    Log.d("TAG" , "ID =  " + id);

                    b.putString("textViewId", String.valueOf(id));

                        Dashboard.putExtras(b);
                        startActivity(Dashboard);



                } else {
                    loginLayout.startAnimation(shakeAnimation);
                    Toast.makeText(getApplicationContext(), "Username/Password incorrect", Toast.LENGTH_LONG).show();
                    emailid.setText("");
                    pass.setText("");
                }
            }
        });


        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If it is checkec then show password else hide
                // password
                if (isChecked) {

                    show_hide_password.setText(R.string.hide_pwd);// change
                    // checkbox
                    // text

                    pass.setInputType(InputType.TYPE_CLASS_TEXT);
                    pass.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                } else {
                    show_hide_password.setText(R.string.show_pwd);// change
                    // checkbox
                    // text

                    pass.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    pass.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password

                }
            }
        });

        // Set check listener over checkbox for showing and hiding password

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
               // intent.setCustomAnimations(R.anim.right_enter, R.anim.left_out);
                loginLayout.setAnimation(anim_to_left);
                startActivity(intent);

            }
        });

    }
}



