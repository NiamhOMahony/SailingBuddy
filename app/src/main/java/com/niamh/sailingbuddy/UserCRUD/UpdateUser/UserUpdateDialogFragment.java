package com.niamh.sailingbuddy.UserCRUD.UpdateUser;

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
import com.niamh.sailingbuddy.Utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;


public class UserUpdateDialogFragment extends DialogFragment {

    /*
     * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
     */
        //Declaring Variables
        private static UserUpdateListener userUpdateListener;
        private static long userId;
        private static int position;

        private User mUser;

        //Declaring Variables
        private EditText nameEditText;
        private EditText emailEditText;
        private EditText phoneEditText;
        private ImageView userImageView;
        private View typeIndicatorView;

        private TextView typeTextView;

        ImageView backImageView;
        ImageView updateImageView;

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;


    private static final int RESULT_LOAD_IMAGE = 1;
        private static final int REQUEST_IMAGE_CAPTURE = 2;

        private Uri imageUri;

        private Bitmap imgToStore;

        SharedPreferences sharedpreferences;
        String typeK = "TYPE_KEY";

        private String selectedSafetyColor;

        private DatabaseQueryClass databaseQueryClass;

        public UserUpdateDialogFragment() {
            // Required empty public constructor
        }


    //when a new instance of the create fragment is opened it processes the below
    public static UserUpdateDialogFragment newInstance(long subId, UserUpdateListener listener) {
        //added difference of declaring 3 new variables
        userId = subId;
        userUpdateListener = listener;

        UserUpdateDialogFragment userUpdateDialogFragment = new UserUpdateDialogFragment();
        Bundle args = new Bundle();
        userUpdateDialogFragment.setArguments(args);
        return userUpdateDialogFragment;
    }

    public static UserUpdateDialogFragment newInstance(int subId, int itemPosition, UserUpdateListener listener) {
        //added difference of declaring 3 new variables
        userId = subId;
        position = itemPosition;
        userUpdateListener = listener;

        UserUpdateDialogFragment userUpdateDialogFragment = new UserUpdateDialogFragment();
        Bundle args = new Bundle();
        userUpdateDialogFragment.setArguments(args);
        return userUpdateDialogFragment;
        }


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_user_update_dialog, container, false);

            //Stop Keyboard automatically popping up because of edit text
            //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
            this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sharedpreferences = requireActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String type = sharedpreferences.getString(typeK, "");


        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_left);
        anim_from_right = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_from_right);
        anim_to_left = AnimationUtils.loadAnimation(getActivity(), R.anim.left_out);
        anim_to_right = AnimationUtils.loadAnimation(getActivity(), R.anim.right_out);
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);


        /*when the update button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
            nameEditText = view.findViewById(R.id.nameTextView);
            emailEditText = view.findViewById(R.id.emailEditText);
            phoneEditText = view.findViewById(R.id.phoneEditText);
            typeTextView = view.findViewById(R.id.typeTextView);
            userImageView = view.findViewById(R.id.userImageView);
            typeIndicatorView = view.findViewById(R.id.typeIndicatorView);


            updateImageView = view.findViewById(R.id.createImageView);
            backImageView = view.findViewById(R.id.backImageView);

            databaseQueryClass = new DatabaseQueryClass(getContext());

            assert getArguments() != null;
            String title = getArguments().getString(Config.TITLE);
            Objects.requireNonNull(getDialog()).setTitle(title);

            mUser = databaseQueryClass.getUserById(userId);

            //instead of getting the values like in create were setting the values to our updated
            nameEditText.setText(mUser.getName());
            emailEditText.setText(mUser.getEmailId());
            phoneEditText.setText(mUser.getMobNo());
            userImageView.setImageBitmap(mUser.getImage());
            imgToStore = mUser.getImage();
            typeTextView.setText(mUser.getType());

        TextView titleTextView = view.findViewById(R.id.TitleTextView);
        TextView admin = view.findViewById(R.id.admin);

        if(type.equals("Instructor")){
            nameEditText.setEnabled(false);
            emailEditText.setEnabled(false);
            phoneEditText.setEnabled(false);
            userImageView.setEnabled(false);
        }

        //Animations
        backImageView.setAnimation(anim_from_top);
        updateImageView.setAnimation(anim_from_top);
        titleTextView.setAnimation(anim_from_top);
        typeIndicatorView.setAnimation(anim_from_left);
        titleTextView.setAnimation(anim_from_left);
        typeTextView.setAnimation(anim_from_left);
        nameEditText.setAnimation(anim_from_left);
        emailEditText.setAnimation(anim_from_left);
        phoneEditText.setAnimation(anim_from_left);
        admin.setAnimation(anim_from_left);
        updateImageView.setAnimation(anim_from_button);


            selectedSafetyColor = "#84B4C8";
            setTypeIndicatorColor();

            updateImageView.setOnClickListener(v -> updateUser());

//if the cancel button is pressed we return to the view
            backImageView.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).dismiss());

            requestPermission();

            userImageView.setOnClickListener(v -> {

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
        //On start up of the Update fragment section of the application these ruels are followed
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

        public void updateUser() {
            if (!nameEditText.getText().toString().isEmpty() &&
                    !phoneEditText.getText().toString().isEmpty() &&
                    !emailEditText.getText().toString().isEmpty() &&
                    !typeTextView.getText().toString().isEmpty() &&
                    userImageView.getDrawable() != null &&
                    imgToStore != null) {

                String stringName = nameEditText.getText().toString();
                String stringEmailId = emailEditText.getText().toString();
                String stringMobileNumber = phoneEditText.getText().toString();
                String stringType = typeTextView.getText().toString();
                String password = mUser.getPassword();


                mUser.setName(stringName);
                mUser.setEmailId(stringEmailId);
                mUser.setMobNo(stringMobileNumber);
                mUser.setPassword(password);
                mUser.setType(stringType);
                mUser.setImage(imgToStore);

                long id = databaseQueryClass.updateUser(mUser);

                if (id > 0) {
                    userUpdateListener.onUserInfoUpdate(mUser, position);
                    Objects.requireNonNull(getDialog()).dismiss();
                }

            }
        }

        private void setTypeIndicatorColor(){
            GradientDrawable gradientDrawable = (GradientDrawable) typeIndicatorView.getBackground();
            gradientDrawable.setColor(Color.parseColor(selectedSafetyColor));
        }
    }