package com.niamh.sailingbuddy.SessionCRUD.UpdateSession;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SessionUpdateDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionUpdateDialogFragment extends DialogFragment {
    //Updating existing Notes

    //Decalring variables
    private static SessionUpdateListener sessionUpdateListener;
    private static long sessionId;
    private static int position;

    private Session mSession;

    private ImageView updateImageView;
    private ImageView backImageView;

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

    private DatabaseQueryClass databaseQueryClass;

    private DatePickerDialog datePickerDialog;
    int hour, minute;

    public SessionUpdateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static SessionUpdateDialogFragment newInstance(long subId, int pos, SessionUpdateListener listener){
        //added difference of declaring 3 new variables
        sessionId = subId;
        position = pos;
        sessionUpdateListener = listener;

        SessionUpdateDialogFragment sessionUpdateDialogFragment = new SessionUpdateDialogFragment();
        Bundle args = new Bundle();
        sessionUpdateDialogFragment.setArguments(args);
        return sessionUpdateDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session_update_dialog, container, false);

        /*when the update button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/

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

        updateImageView = view.findViewById(R.id.updateImageView);
        backImageView = view.findViewById(R.id.backImageView);

        databaseQueryClass = new DatabaseQueryClass(getContext());

        initDatePicker();

        String title = getArguments().getString(Config.TITLE);
        getDialog().setTitle(title);

        mSession = databaseQueryClass.getSessionById(sessionId);
        //instead of getting the values like in create wer setting the values to our updated
        nameEditText.setText(mSession.getInstructorName());
        dateTextView.setText(String.valueOf(mSession.getDate()));
        noStudentsEditText.setText(mSession.getNoStudents());
        launchEditText.setText(mSession.getLaunchTime());
        recoveryEditText.setText(mSession.getRecoveryTime());
        landEditText.setText(mSession.getLandActvity());
        waterEditText.setText(mSession.getWaterActivity());
        areaEditText.setText(mSession.getSailArea());
        highEditText.setText(mSession.getHighTide());
        lowEditText.setText(mSession.getLowTide());
        weatherEditText.setText(mSession.getWeather());

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



        updateImageView.setOnClickListener(new View.OnClickListener() {
            //when we return to view we will get our new values
            @Override
            public void onClick(View view) {
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

                mSession.setInstructorName(nameString);
                mSession.setDate(dateString);
                mSession.setLevel(levelString);
                mSession.setNoStudents(noStudentsString);
                mSession.setLaunchTime(launchString);
                mSession.setRecoveryTime(recoveryString);
                mSession.setLandActvity(landString);
                mSession.setWaterActivity(waterString);
                mSession.setSailArea(areaString);
                mSession.setHighTide(highString);
                mSession.setLowTide(lowString);
                mSession.setWeather(weatherString);

                long id = databaseQueryClass.updateSession(mSession);

                if(id>0){
                    sessionUpdateListener.onSessionInfoUpdate(mSession, position);
                    getDialog().dismiss();
                }
            }
        });
        //if the cancel button is pressed we return to the view
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return view;
    }
    //On start up of the Update fragment section of the application these ruels are followed
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            //noinspection ConstantConditions
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