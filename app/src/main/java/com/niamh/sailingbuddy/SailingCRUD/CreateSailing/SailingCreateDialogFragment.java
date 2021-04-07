package com.niamh.sailingbuddy.SailingCRUD.CreateSailing;

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
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.Utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class SailingCreateDialogFragment extends DialogFragment {
    //Creating a new Sailing Equipment

    //adding in external listener
    private static SailingCreateListener sailingCreateListener;

    //declaring variables
    private Spinner typeSpinner;
    private EditText descriptionEditText;
    private Spinner availableSpinner;
    private Spinner faultSpinner;
    private TextView signOutSpinner;
    private TextView signOutFSpinner;
    private EditText faultEditText;
    private ImageView sailingImageView;
    private TextView availableTextView;
    private TextView faultTextView;
    private View typeIndicatorView;

    private Bitmap imgToStore;

    //giving variables values
    private String typeString = "";
    private String descriptionString = "";
    private String availableString = "";
    private String availuserString = "";
    private String faultString = "";
    private String faultUserString = "";
    private String faultdescString = "";

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    private DatabaseQueryClass databaseQueryClass;
    SharedPreferences sharedpreferences;
    String IdK = "ID_KEY";

    private String selectedSailingColor;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private Uri imageUri;

    public SailingCreateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static SailingCreateDialogFragment newInstance(SailingCreateListener listener){
        sailingCreateListener = listener;
        SailingCreateDialogFragment sailingCreateDialogFragment = new SailingCreateDialogFragment();
        Bundle args = new Bundle();
        sailingCreateDialogFragment.setArguments(args);

        return sailingCreateDialogFragment;
    }


    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
    /*when the create button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sailing_create_dialog, container, false);

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

        typeSpinner = view.findViewById(R.id.typeEditText);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        availableSpinner = view.findViewById(R.id.availableSpinner);
        signOutSpinner = view.findViewById(R.id.signOutSpinner);
        faultSpinner = view.findViewById(R.id.faultSpinner);
        signOutFSpinner = view.findViewById(R.id.signOutFSpinner);
        faultEditText = view.findViewById(R.id.faultEditText);
        sailingImageView = view.findViewById(R.id.userImageView);
        availableTextView = view.findViewById(R.id.signedOutTextView);
        faultTextView = view.findViewById(R.id.signedOutFTextView);
        typeIndicatorView = view.findViewById(R.id.typeIndicatorView);

        ImageView createImageView = view.findViewById(R.id.createImageView);
        ImageView backImageView = view.findViewById(R.id.backImageView);
        TextView titleTextView = view.findViewById(R.id.TitleTextView);
        TextView availableTitleTextView = view.findViewById(R.id.availableTextView);
        TextView faultTitleTextView = view.findViewById(R.id.faultTextView);


        //Animations
        backImageView.setAnimation(anim_from_top);
        createImageView.setAnimation(anim_from_top);
        titleTextView.setAnimation(anim_from_top);
        typeIndicatorView.setAnimation(anim_from_left);
        typeSpinner.setAnimation(anim_from_left);
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

        String title = requireArguments().getString(Config.TITLE);
        Objects.requireNonNull(getDialog()).setTitle(title);

        selectedSailingColor = "#EDEDED";
        setTypeIndicatorColor();

        databaseQueryClass = new DatabaseQueryClass(getContext());

        sharedpreferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        int Id = sharedpreferences.getInt(IdK,1);
        User mUser = databaseQueryClass.getUserById(Id);

        signOutSpinner.setText(mUser.getName());
        signOutFSpinner.setText(mUser.getName());

        //How to use Spinner and its setOnItemClickListener() event https://www.youtube.com/watch?v=LU3XyZWO8Z0
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Changing backgroud color inspo | Android Development | Notes App | Tutorial #5 | Note Color | Android Studio https://www.youtube.com/watch?v=Xpd9E4CD84Q&t=1096s
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        selectedSailingColor = "#FEA3AA";
                        setTypeIndicatorColor();
                        break;
                    case 1:
                        selectedSailingColor = "#F8B88B";
                        setTypeIndicatorColor();
                        break;
                    case 2:
                        selectedSailingColor = "#FAF88A";
                        setTypeIndicatorColor();
                        break;
                    case 3:
                        selectedSailingColor = "#BAED91";
                        setTypeIndicatorColor();
                        break;
                    case 4:
                        selectedSailingColor = "#B2CEFE";
                        setTypeIndicatorColor();
                        break;
                    case 5:
                        selectedSailingColor = "#F2A2E8";
                        setTypeIndicatorColor();
                        break;
                    case 6:
                        selectedSailingColor = "#FFDAC1";
                        setTypeIndicatorColor();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSailingColor = "#A4A4A4";
                setTypeIndicatorColor();
            }
        });

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

        createImageView.setOnClickListener(v -> {

            if(!typeSpinner.getSelectedItem().toString().isEmpty() &&
                    !descriptionEditText.getText().toString().isEmpty() &&
                    !availableSpinner.getSelectedItem().toString().isEmpty() &&
                    !faultSpinner.getSelectedItem().toString().isEmpty() &&
                    sailingImageView.getDrawable()!= null &&
                    imgToStore!= null){

                typeString = typeSpinner.getSelectedItem().toString();
                descriptionString = descriptionEditText.getText().toString();
                availableString = availableSpinner.getSelectedItem().toString();
                availuserString = signOutSpinner.getText().toString();
                faultString = faultSpinner.getSelectedItem().toString();
                faultUserString = signOutFSpinner.getText().toString();
                faultdescString = faultEditText.getText().toString();

                Sailing sailing = new Sailing(-1, typeString, descriptionString, availableString, availuserString,
                        faultString, faultUserString, faultdescString, imgToStore); //sailing color

                DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(getContext());

                long id = databaseQueryClass.insertSailing(sailing);

                if (id > 0) {
                    sailing.setId(id);
                    sailingCreateListener.onSailingCreated(sailing);
                    Objects.requireNonNull(getDialog()).dismiss();
                }


            }else {
                Toast.makeText(getActivity(), "Please enter all details", Toast.LENGTH_SHORT).show();
            }
        });
        backImageView.setOnClickListener(v -> Objects.requireNonNull(getDialog()).dismiss());


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
        gradientDrawable.setColor(Color.parseColor(selectedSailingColor));
    }
}

