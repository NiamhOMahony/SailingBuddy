package com.niamh.sailingbuddy.SessionCRUD.CreateSession;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.Calendar;
import java.util.Locale;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class SessionCreateDialogFragment extends DialogFragment {
    //Creating a new Session

    //adding in external listener
    private static SessionCreateListener sessionCreateListener;

    //declaring variables
    private ImageView backImageView;
    private ImageView createImageView;
    private TextView titleTextView;
    private TextView dateTextView;
    private View typeIndicatorView;
    private TextView nameEditText;
    private Spinner levelSpinner;
    private EditText noStudentsEditText;
    private EditText landEditText;
    private  EditText waterEditText;
    private EditText areaEditText;
    private TextView highEditText;
    private TextView lowEditText;
    private TextView launchEditText;
    private TextView recoveryEditText;
    private EditText weatherEditText;

    //giving variables values
    private String nameString = "";
    private String dateString ="";
    private String levelString = "";
    private String noStudentsString = "";
    private String landString = "";
    private String waterString = "";
    private String areaString = "";
    private String highString = "";
    private String lowString = "";
    private String launchString = "";
    private String recoveryString = "";
    private String weatherString = "";

    SharedPreferences sharedpreferences;
    String name = "NAME_KEY";

    private DatePickerDialog datePickerDialog;
    int hour, minute;

    public SessionCreateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static SessionCreateDialogFragment newInstance(String title, SessionCreateListener listener){
        sessionCreateListener = listener;
        SessionCreateDialogFragment sessionCreateDialogFragment = new SessionCreateDialogFragment();
        Bundle args = new Bundle();
        sessionCreateDialogFragment.setArguments(args);

        return sessionCreateDialogFragment;
    }


    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    /*when the create button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_session_create_dialog, container, false);

        dateTextView = view.findViewById(R.id.dateTextView);
        titleTextView  = view.findViewById(R.id.titleTextView);
        typeIndicatorView  = view.findViewById(R.id.typeIndicatorView);
        nameEditText  = view.findViewById(R.id.nameTextView);
        noStudentsEditText  = view.findViewById(R.id.noStudentsEditText);
        levelSpinner  = view.findViewById(R.id.levelSpinner);
        launchEditText  = view.findViewById(R.id.launchEditText);
        recoveryEditText  = view.findViewById(R.id.recoveryEditText);
        landEditText  = view.findViewById(R.id.landEditText);
        waterEditText  = view.findViewById(R.id.waterEditText);
        areaEditText  = view.findViewById(R.id.areaEditText);
        highEditText  = view.findViewById(R.id.highEditText);
        lowEditText  = view.findViewById(R.id.lowEditText);
        weatherEditText  = view.findViewById(R.id.weatherEditText);
        createImageView = view.findViewById(R.id.createImageView);
        backImageView = view.findViewById(R.id.backImageView);

        sharedpreferences = requireActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String nameText =sharedpreferences.getString(name, "") ;

        nameEditText.setText(nameText);

        String title = getArguments().getString(Config.TITLE);
        getDialog().setTitle(title);

        initDatePicker();

        dateTextView.setText(getTodaysDate());

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        highEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highTimePicker(v);
            }
        });

        lowEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lowTimePicker(v);
            }
        });

        launchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchTimePicker(v);
            }
        });

        recoveryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoveryTimePicker(v);
            }
        });


        //when the create image is pressed it creates a new row in the database
        createImageView.setOnClickListener(view12 -> {

            nameString = nameEditText.getText().toString();
            dateString = dateTextView.getText().toString();
            levelString = levelSpinner.getSelectedItem().toString();
            noStudentsString = noStudentsEditText.getText().toString();
            launchString = launchEditText.getText().toString();
            recoveryString = recoveryEditText.getText().toString();
            landString = landEditText.getText().toString();
            waterString = waterEditText.getText().toString();
            areaString = areaEditText.getText().toString();
            highString = highEditText.getText().toString();
            lowString = lowEditText.getText().toString();
            weatherString = weatherEditText.getText().toString();

            Session session = new Session(-1, nameString, dateString, levelString, noStudentsString, launchString,
                    recoveryString, landString, waterString, areaString, highString, lowString, weatherString);

            DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(getContext());

            long id = databaseQueryClass.insertSession(session);

            if(id>0){
                session.setId(id);
                sessionCreateListener.onSessionCreated(session);
                getDialog().dismiss();

            }
        });

        //when the back button is pressed moves out of the fragmet back

        backImageView.setOnClickListener(view1 -> getDialog().dismiss());
        return view;
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

    //date time picker inspiration - Pop Up Date Picker Android Studio Tutorial https://www.youtube.com/watch?v=qCoidM98zNk

    private String getTodaysDate()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }


    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                dateTextView.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);
        //datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }


    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }

    public void highTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                highEditText.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

         int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    public void lowTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                lowEditText.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    public void launchTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                launchEditText.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    public void recoveryTimePicker(View view) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour = selectedHour;
                minute = selectedMinute;
                recoveryEditText.setText(String.format(Locale.getDefault(), "%02d:%02d",hour, minute));
            }
        };

        int style = AlertDialog.THEME_HOLO_DARK;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, true);

        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }



}