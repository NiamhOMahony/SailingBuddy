package com.niamh.sailingbuddy.UserCRUD.CreateUser;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.niamh.sailingbuddy.Utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;


public class UserCreateDialogFragment extends DialogFragment {

    //Creating a new User

    //adding in external listener
    private static UserCreateListener userCreateListener;

    //declaring variables
    private Spinner typeSpinner;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText phoneEditText;
    private EditText passEditText;
    private EditText passConfEditText;
    private ImageView userImageView;
    private View typeIndicatorView;


    //giving variables values
    private String typeString = "";
    private String nameString = "";
    private String emailString = "";
    private String phoneString = "";
    private String passwordString = "";

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    private String selectedUserColor;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Bitmap imgToStore;

    private Uri imageUri;

    SharedPreferences sharedpreferences;
    String name = "NAME_KEY";

    public UserCreateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static UserCreateDialogFragment newInstance(UserCreateListener listener){
        userCreateListener = listener;
        UserCreateDialogFragment userCreateDialogFragment = new UserCreateDialogFragment();
        Bundle args = new Bundle();
        userCreateDialogFragment.setArguments(args);

        return userCreateDialogFragment;
    }


    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
    /*when the create button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_user_create_dialog, container, false);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_left);
        anim_from_right = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_right);
        anim_to_left = AnimationUtils.loadAnimation(getActivity(), R.anim.left_out);
        anim_to_right = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

        typeSpinner = view.findViewById(R.id.typeTextView);
        nameEditText = view.findViewById(R.id.nameTextView);
        emailEditText = view.findViewById(R.id.emailEditText);
        phoneEditText = view.findViewById(R.id.phoneEditText);
        passEditText = view.findViewById(R.id.passEditText);
        passConfEditText = view.findViewById(R.id.passConfEditText);
        typeIndicatorView = view.findViewById(R.id.typeIndicatorView);

        userImageView = view.findViewById(R.id.userImageView);

        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        ImageView createImageView = view.findViewById(R.id.createImageView);
        ImageView backImageView = view.findViewById(R.id.backImageView);
        TextView titleTextView = view.findViewById(R.id.TitleTextView);
        TextView admin = view.findViewById(R.id.admin);

        String title = requireArguments().getString(Config.TITLE);
        Objects.requireNonNull(getDialog()).setTitle(title);

        selectedUserColor = "#EDEDED";
        setTypeIndicatorColor();

        //set annimations
        backImageView.setAnimation(anim_from_top);
        createImageView.setAnimation(anim_from_top);
        titleTextView.setAnimation(anim_from_top);
        typeIndicatorView.setAnimation(anim_from_left);
        nameEditText.setAnimation(anim_from_left);
        emailEditText.setAnimation(anim_from_left);
        phoneEditText.setAnimation(anim_from_left);
        passEditText.setAnimation(anim_from_left);
        passConfEditText.setAnimation(anim_from_left);
        admin.setAnimation(anim_from_left);
        typeSpinner.setAnimation(anim_from_left);
        userImageView.setAnimation(anim_from_button);




        createImageView.setOnClickListener(v -> {

            if(!typeSpinner.getSelectedItem().toString().isEmpty() &&
                    !nameEditText.getText().toString().isEmpty() &&
                    !emailEditText.getText().toString().isEmpty() &&
                    !phoneEditText.getText().toString().isEmpty() &&
                    !phoneEditText.getText().toString().isEmpty() &&
                    userImageView.getDrawable()!= null &&
                    imgToStore!= null){


                nameString = nameEditText.getText().toString();
                emailString = emailEditText.getText().toString();
                phoneString = phoneEditText.getText().toString();
                passwordString = passEditText.getText().toString();
                typeString = typeSpinner.getSelectedItem().toString();
                String passConf = passConfEditText.getText().toString();

                if(passwordString.equals(passConf)){

                    User user = new User(-1, nameString, emailString,
                            phoneString, passwordString, typeString, imgToStore);

                    DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(getContext());

                    long id = databaseQueryClass.insertUser(user);

                    if (id > 0) {
                        user.setID((int) id);
                        userCreateListener.onUserCreated(user);
                        Objects.requireNonNull(getDialog()).dismiss();
                    }
                }else {
                    Toast.makeText(getActivity(), "Password is not matching", Toast.LENGTH_SHORT).show();
                    //registerLayout.setAnimation(shakeAnimation);
                    passEditText.setText("");
                    passConfEditText.setText("");
                }
            }else {
                Toast.makeText(getActivity(), "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });

        backImageView.setOnClickListener(v -> Objects.requireNonNull(
                getDialog()).dismiss());


        requestPermission();

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
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

        return view;
    }

    private void camera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        ContentValues values = new ContentValues();

        ContentResolver resolver = requireActivity().getContentResolver();
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
                ContentResolver resolver = requireActivity().getContentResolver();
                imgToStore = MediaStore.Images.Media.getBitmap(resolver, imgUri);
                userImageView.setImageBitmap(imgToStore);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK ) {
            Uri imgUri = imageUri;
            try {
                ContentResolver resolver = requireActivity().getContentResolver();
                imgToStore = MediaStore.Images.Media.getBitmap(resolver, imgUri);
                userImageView.setImageBitmap(imgToStore);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void requestPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getContext(), "All permissions are granted!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void setTypeIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) typeIndicatorView.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedUserColor));
    }
}