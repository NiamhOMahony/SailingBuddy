package com.niamh.sailingbuddy.UserCRUD;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.IOException;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText name;
    private EditText emailId;
    private EditText mobileNumber;
    private EditText pass;
    private EditText confpass;
    private Spinner typeSpinner;
    private ImageView userImageView;

    private CheckBox terms_conditions;

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    String adminPassword;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Bitmap imgToStore;

    private Uri imageUri;

        DatabaseQueryClass databaseQueryClass;
        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_register);

            databaseQueryClass = new DatabaseQueryClass(this);

            //Stop Keyboard automatically popping up because of edit text
            //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            FrameLayout registerLayout = findViewById(R.id.registerActivity);
            TextView signUpTextView = findViewById(R.id.signUpTextView);

            name = findViewById(R.id.firstName);
            emailId = findViewById(R.id.userEmailId);
            mobileNumber = findViewById(R.id.mobileNumber);
            pass = findViewById(R.id.password);
            confpass = findViewById(R.id.confirmPassword);
            Button save = findViewById(R.id.signUpBtn);
            TextView alreadyUser = findViewById(R.id.already_user);
            terms_conditions = findViewById(R.id.terms_conditions);
            typeSpinner = findViewById(R.id.typeTextView);
            userImageView = findViewById(R.id.userImageView);

            View viewFName = findViewById(R.id.viewFName);
            View viewLName = findViewById(R.id.viewLName);
            View viewEmail = findViewById(R.id.viewEmail);
            View viewMobir = findViewById(R.id.viewMobie);
            View viewPass = findViewById(R.id.viewPass);
            View viewConf = findViewById(R.id.viewConf);
            View viewType = findViewById(R.id.viewType);

            ConstraintLayout type = findViewById(R.id.type);
            ConstraintLayout TC = findViewById(R.id.TC);

            //Load Animations
            anim_from_button = AnimationUtils.loadAnimation(this, R.anim.anim_from_bottom);
            anim_from_top = AnimationUtils.loadAnimation(this, R.anim.anim_from_top);
            anim_from_left = AnimationUtils.loadAnimation(this, R.anim.anim_from_left);
            anim_from_right = AnimationUtils.loadAnimation(this, R.anim.anim_from_right);
            anim_to_left = AnimationUtils.loadAnimation(this, R.anim.left_out);
            anim_to_right = AnimationUtils.loadAnimation(this, R.anim.right_out);
            shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

            //Animate OnCreate

            signUpTextView.setAnimation(anim_from_top);

            name.setAnimation(anim_from_left);
            emailId.setAnimation(anim_from_left);
            mobileNumber.setAnimation(anim_from_left);
            pass.setAnimation(anim_from_left);
            confpass.setAnimation(anim_from_left);

            save.setAnimation(anim_from_button);
            alreadyUser.setAnimation(anim_from_button);

            viewFName.setAnimation(anim_from_left);
            viewLName.setAnimation(anim_from_left);
            viewEmail.setAnimation(anim_from_left);
            viewMobir.setAnimation(anim_from_left);
            viewPass.setAnimation(anim_from_left);
            viewConf.setAnimation(anim_from_left);
            viewType.setAnimation(anim_from_left);

            type.setAnimation(anim_from_left);
            TC.setAnimation(anim_from_left);

            typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            break;
                        case 1:
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setTitle("Admin Password");

                            // Set up the input
                            final EditText input = new EditText(RegisterActivity.this);
                            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            builder.setView(input);

                                // Set up the buttons
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    adminPassword = input.getText().toString();
                                    if (adminPassword.equals("0000")){
                                        Toast.makeText(RegisterActivity.this, "Password Correct", Toast.LENGTH_SHORT).show();
                                        typeSpinner.setSelection(1);
                                    }else {
                                        Toast.makeText(RegisterActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                                        typeSpinner.setSelection(0);
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    typeSpinner.setSelection(0);
                                    dialog.cancel();
                                }
                            });

                            builder.show();

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            alreadyUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

            //Regster Button Saving all Values to UserSQLite
            save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (!typeSpinner.getSelectedItem().toString().isEmpty() &&
                            !name.getText().toString().isEmpty() &&
                            !emailId.getText().toString().isEmpty() &&
                            !mobileNumber.getText().toString().isEmpty() &&
                            !pass.getText().toString().isEmpty() &&
                            !confpass.getText().toString().isEmpty() &&
                            userImageView.getDrawable() != null &&
                            imgToStore != null) {


                        User user = new User();

                        String stringFirstName = name.getText().toString();
                        String stringEmailId = emailId.getText().toString();
                        String stringMobileNumber = mobileNumber.getText().toString();
                        String stringPass = pass.getText().toString();
                        String stringConfPass = confpass.getText().toString();
                        String stringType = typeSpinner.getSelectedItem().toString();
                        if (stringEmailId.contains("@") && stringEmailId.contains(".")) {
                            if (terms_conditions.isChecked()) {
                                if (stringPass.equals(stringConfPass) && terms_conditions.isChecked()) {

                                    user.setName(stringFirstName);
                                    user.setEmailId(stringEmailId);
                                    user.setMobNo(stringMobileNumber);
                                    user.setPassword(stringPass);
                                    user.setType(stringType);
                                    user.setImage(imgToStore);

                                    long val = databaseQueryClass.insertUser(user);

                                    if (val > 0) {
                                        Toast.makeText(RegisterActivity.this, "You have registered", Toast.LENGTH_SHORT).show();
                                        Intent moveToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(moveToLogin);
                                    }

                                }else {
                                    Toast.makeText(RegisterActivity.this, "Password is not matching", Toast.LENGTH_SHORT).show();
                                    registerLayout.setAnimation(shakeAnimation);
                                    pass.setText("");
                                    confpass.setText("");
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "Please Agree to Terms and Conditions", Toast.LENGTH_SHORT).show();
                                terms_conditions.setAnimation(shakeAnimation);
                            }


                        } else {
                            Toast.makeText(RegisterActivity.this, "Please enter a Valid Email", Toast.LENGTH_SHORT).show();
                            registerLayout.setAnimation(shakeAnimation);
                        }

                    }else {
                        Toast.makeText(RegisterActivity.this, "Please enter all details", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.terms_and_conditions);

            ImageView terms = findViewById(R.id.sure);
            terms.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAboutDialog();
                }
            });
            requestPermission();

            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(RegisterActivity.this);
                    dialog.setContentView(R.layout.custom_dialog_box);
                    dialog.setTitle("Alert Dialog View");
                    Button btnExit = dialog.findViewById(R.id.btnExit);
                    btnExit.setOnClickListener(new View.OnClickListener() {
                        @Override public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.btnChoosePath)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override public void onClick(View v) {
                                    gallery();
                                }
                            });
                    dialog.findViewById(R.id.btnTakePhoto)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override public void onClick(View v) {
                                    camera();
                                    //dialog.dismiss();
                                }
                            });

                    // show dialog on screen
                    dialog.show();
                }
            });

        }

    private void camera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        ContentValues values = new ContentValues();

        ContentResolver resolver = getContentResolver();
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    private void gallery() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),RESULT_LOAD_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!= null) {
            Uri imgUri = data.getData();
            try {
                ContentResolver resolver = getContentResolver();
                imgToStore = MediaStore.Images.Media.getBitmap(resolver, imgUri);
                userImageView.setImageBitmap(imgToStore);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK ) {
            Uri imgUri = imageUri;
            try {
                ContentResolver resolver = getContentResolver();
                imgToStore = MediaStore.Images.Media.getBitmap(resolver, imgUri);
                userImageView.setImageBitmap(imgToStore);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(RegisterActivity.this, "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(RegisterActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.terms_and_conditions);
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    }
