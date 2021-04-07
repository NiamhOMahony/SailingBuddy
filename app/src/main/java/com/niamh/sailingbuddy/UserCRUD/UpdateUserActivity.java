package com.niamh.sailingbuddy.UserCRUD;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class UpdateUserActivity extends AppCompatActivity {

    /*
     * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
     */
    //Update Existing User
    String passwordK = "PASSWORD_KEY";
    String IdK = "ID_KEY";

    //Declaring Variables
    private static UserUpdateListener2 userUpdateListener2;
    private static long userId;

    private User mUser;

    //Declaring Variables
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private ImageView userImageView;
    private View typeIndicatorView;

    private TextView typeTextView;
    private TextView typeSpinner;

    ImageView backImageView;
    ImageView updateImageView;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Uri imageUri;

    private Bitmap imgToStore;

    SharedPreferences sharedpreferences;

    private String selectedSafetyColor;

    private DatabaseQueryClass databaseQueryClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /*when the update button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
        nameEditText = findViewById(R.id.nameTextView);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        typeSpinner = findViewById(R.id.typeTextView);
        userImageView = findViewById(R.id.userImageView);
        typeIndicatorView = findViewById(R.id.typeIndicatorView);

        typeTextView = findViewById(R.id.typeTextView);

        updateImageView = findViewById(R.id.createImageView);
        backImageView = findViewById(R.id.backImageView);

        databaseQueryClass = new DatabaseQueryClass(this);

        sharedpreferences = this.getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        userId = sharedpreferences.getInt(IdK, 1);

        mUser = databaseQueryClass.getUserById(userId);

        //instead of getting the values like in create were setting the values to our updated
        nameEditText.setText(mUser.getName());
        emailEditText.setText(mUser.getEmailId());
        phoneEditText.setText(mUser.getMobNo());
        userImageView.setImageBitmap(mUser.getImage());
        imgToStore = mUser.getImage();
        typeSpinner.setText(mUser.getType());

        selectedSafetyColor = "#84B4C8";
        setTypeIndicatorColor();

        updateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
                Intent profileIntent = new Intent(UpdateUserActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });



//if the cancel button is pressed we return to the view
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(UpdateUserActivity.this, ProfileActivity.class);
                startActivity(backIntent);
            }
        });


        requestPermission();

        userImageView.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(this);
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
        });


    }

    private void camera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        ContentValues values = new ContentValues();

        ContentResolver resolver = this.getContentResolver();
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
                ContentResolver resolver = this.getContentResolver();
                imgToStore = MediaStore.Images.Media.getBitmap(resolver, imgUri);
                userImageView.setImageBitmap(imgToStore);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK ) {
            Uri imgUri = imageUri;
            try {
                ContentResolver resolver = this.getContentResolver();
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
                            Toast.makeText(UpdateUserActivity.this, "All permissions are granted!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UpdateUserActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    public void updateUser() {
        if (!nameEditText.getText().toString().isEmpty() &&
                !phoneEditText.getText().toString().isEmpty() &&
                !emailEditText.getText().toString().isEmpty() &&
                !typeSpinner.getText().toString().isEmpty()
                    /*userImageView.getDrawable() != null &&
                    imgToStore != null*/) {

            String stringFirstName = nameEditText.getText().toString();
            String stringEmailId = emailEditText.getText().toString();
            String stringMobileNumber = phoneEditText.getText().toString();
            String stringType = typeSpinner.getText().toString();
            String password = mUser.getPassword();


            mUser.setName(stringFirstName);
            mUser.setEmailId(stringEmailId);
            mUser.setMobNo(stringMobileNumber);
            mUser.setPassword(password);
            mUser.setType(stringType);
            mUser.setImage(imgToStore);

            // Shared preferences insporation from https://www.journaldev.com/9412/android-shared-preferences-example-tutorial
            int i = mUser.getID();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(IdK, i);

            editor.apply();

            long id = databaseQueryClass.updateUser(mUser);

        }
    }

    private void setTypeIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) typeIndicatorView.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedSafetyColor));
    }
}