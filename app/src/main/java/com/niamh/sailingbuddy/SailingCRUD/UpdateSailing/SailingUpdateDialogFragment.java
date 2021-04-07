package com.niamh.sailingbuddy.SailingCRUD.UpdateSailing;

/*
 * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
 */

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.Utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class SailingUpdateDialogFragment extends DialogFragment {
    //Update Existing Note

    //Declaring Variables
    private static SailingUpdateListener sailingUpdateListener;
    private static long sailingId;

    private Sailing mSailing;

    //Declaring Variables
    private EditText typeEditText;
    private EditText descriptionEditText;
    private Spinner availableSpinner;
    private Spinner faultSpinner;
    private TextView signOutSpinner;
    private TextView signOutFSpinner;
    private EditText faultEditText;
    private ImageView sailingImageView;
    private View typeIndicatorView;

    private TextView availableTextView;
    private TextView faultTextView;

    ImageView backImageView;
    ImageView updateImageView;

    private Bitmap imgToStore;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Uri imageUri;

    private DatabaseQueryClass databaseQueryClass;
    SharedPreferences sharedpreferences;
    String IdK = "ID_KEY";

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    private String selectedSailingColor;

    public SailingUpdateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static SailingUpdateDialogFragment newInstance(long subId, SailingUpdateListener listener) {
        //added difference of declaring 3 new variables
        sailingId = subId;
        sailingUpdateListener = listener;

        SailingUpdateDialogFragment sailingUpdateDialogFragment = new SailingUpdateDialogFragment();
        Bundle args = new Bundle();
        sailingUpdateDialogFragment.setArguments(args);
        return sailingUpdateDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sailing_update_dialog, container, false);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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
        typeEditText = view.findViewById(R.id.typeEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        availableSpinner = view.findViewById(R.id.availableSpinner);
        signOutSpinner = view.findViewById(R.id.signOutSpinner);
        faultSpinner = view.findViewById(R.id.faultSpinner);
        signOutFSpinner = view.findViewById(R.id.signOutFSpinner);
        faultEditText = view.findViewById(R.id.faultEditText);
        sailingImageView = view.findViewById(R.id.userImageView);
        typeIndicatorView = view.findViewById(R.id.typeIndicatorView);

        availableTextView = view.findViewById(R.id.signedOutTextView);
        faultTextView = view.findViewById(R.id.signedOutFTextView);

        updateImageView = view.findViewById(R.id.createImageView);
        backImageView = view.findViewById(R.id.backImageView);

        databaseQueryClass = new DatabaseQueryClass(getContext());

        assert getArguments() != null;
        String title = getArguments().getString(Config.TITLE);
        Objects.requireNonNull(getDialog()).setTitle(title);

        mSailing = databaseQueryClass.getSailingById(sailingId);

        databaseQueryClass = new DatabaseQueryClass(getContext());

        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        int Id = sharedpreferences.getInt(IdK,1);
        User mUser = databaseQueryClass.getUserById(Id);

        signOutSpinner.setText(mUser.getName());
        signOutFSpinner.setText(mUser.getName());

        //instead of getting the values like in create were setting the values to our updated
        descriptionEditText.setText(mSailing.getDescription());
        faultEditText.setText(mSailing.getFaultdes());
        sailingImageView.setImageBitmap(mSailing.getImage());
        imgToStore = mSailing.getImage();
        typeEditText.setText(mSailing.getType());

        TextView titleTextView = view.findViewById(R.id.TitleTextView);
        TextView availableTitleTextView = view.findViewById(R.id.availableTextView);
        TextView faultTitleTextView = view.findViewById(R.id.faultTextView);

        //Animations
        backImageView.setAnimation(anim_from_top);
        updateImageView.setAnimation(anim_from_top);
        titleTextView.setAnimation(anim_from_top);
        typeIndicatorView.setAnimation(anim_from_left);
        titleTextView.setAnimation(anim_from_left);
        descriptionEditText.setAnimation(anim_from_left);
        availableTitleTextView.setAnimation(anim_from_left);
        availableSpinner.setAnimation(anim_from_left);
        faultTitleTextView.setAnimation(anim_from_left);
        faultSpinner.setAnimation(anim_from_left);
        sailingImageView.setAnimation(anim_from_button);

        signOutSpinner.setVisibility(View.GONE);
        availableTextView.setVisibility(View.GONE);
        signOutFSpinner.setVisibility(View.GONE);
        faultEditText.setVisibility(View.GONE);
        faultTextView.setVisibility(View.GONE);

        selectedSailingColor = "#84B4C8";
        setTypeIndicatorColor();


        availableSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        signOutSpinner.setVisibility(View.GONE);
                        availableTextView.setVisibility(View.GONE);
                        break;
                    case 1:
                        signOutSpinner.setVisibility(View.VISIBLE);
                        availableTextView.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                signOutSpinner.setVisibility(View.GONE);
                availableTextView.setVisibility(View.GONE);
            }
        });

        faultSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

                switch (i){
                    case 0:
                        signOutFSpinner.setVisibility(View.GONE);
                        faultEditText.setVisibility(View.GONE);
                        faultTextView.setVisibility(View.GONE);
                        break;
                    case 1:
                        signOutFSpinner.setVisibility(View.VISIBLE);
                        faultEditText.setVisibility(View.VISIBLE);
                        faultTextView.setVisibility(View.VISIBLE);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                signOutFSpinner.setVisibility(View.GONE);
                faultEditText.setVisibility(View.GONE);
                faultTextView.setVisibility(View.GONE);
            }
        });


        updateImageView.setOnClickListener(v -> updateSailing());

//if the cancel button is pressed we return to the view
        backImageView.setOnClickListener(view1 -> Objects.requireNonNull(getDialog()).dismiss());

        requestPermission();

        sailingImageView.setOnClickListener(v -> {

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
                sailingImageView.setImageBitmap(imgToStore);

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK ) {
            Uri imgUri = imageUri;
            try {
                ContentResolver resolver = requireActivity().getContentResolver();
                imgToStore = MediaStore.Images.Media.getBitmap(resolver, imgUri);
                sailingImageView.setImageBitmap(imgToStore);

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
                            Toast.makeText(getContext(), "All permissions are Denied!", Toast.LENGTH_SHORT).show();
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
    public void updateSailing() {
        if (!typeEditText.getText().toString().isEmpty() &&
                !descriptionEditText.getText().toString().isEmpty() &&
                !availableSpinner.getSelectedItem().toString().isEmpty() &&
                !faultSpinner.getSelectedItem().toString().isEmpty() &&
                sailingImageView.getDrawable() != null &&
                imgToStore != null) {

            String typeString = typeEditText.getText().toString();
            String descriptionString = descriptionEditText.getText().toString();
            String availableString = availableSpinner.getSelectedItem().toString();
            String availuserString = signOutSpinner.getText().toString();
            String faultString = faultSpinner.getSelectedItem().toString();
            String faultUserString = signOutFSpinner.getText().toString();
            String faultdescString = faultEditText.getText().toString();
            //not used because causing app to crash ?
            //imgToStore  = sailingImageView.getDrawingCache();

            mSailing.setType(typeString);
            mSailing.setDescription(descriptionString);
            mSailing.setAvailable(availableString);
            mSailing.setAvailuser(availuserString);
            mSailing.setFault(faultString);
            mSailing.setFaultuser(faultUserString);
            mSailing.setFaultdes(faultdescString);
            mSailing.setImage(imgToStore);


            long id = databaseQueryClass.updateSailing(mSailing);

            if (id > 0) {
                sailingUpdateListener.onSailingInfoUpdate(mSailing);
                Objects.requireNonNull(getDialog()).dismiss();
            }

        }
    }

    private void setTypeIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) typeIndicatorView.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedSailingColor));
    }
}
