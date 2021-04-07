package com.niamh.sailingbuddy.NoteCRUD.CreateNote;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.Calendar;

/*
 * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
 */

public class NotesCreateDialogFragment extends DialogFragment {
    //Creating a new Note

    //adding in external listener
    private static NotesCreateListener notesCreateListener;

    //declaring variables
    private EditText titleEditText;
    private EditText subtitleEditText;
    private EditText noteEditText;
    private TextView dateTextView;

    private ImageView backImageView;
    private ImageView createImageView;

    //giving variables values
    private String titleString = "";
    private String subtitlleString = "";
    private String noteString = "";
    private String dateString ="";

    private DatePickerDialog datePickerDialog;

    public NotesCreateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static NotesCreateDialogFragment newInstance(String title, NotesCreateListener listener){
        notesCreateListener = listener;
        NotesCreateDialogFragment notesCreateDialogFragment = new NotesCreateDialogFragment();
        Bundle args = new Bundle();
        notesCreateDialogFragment.setArguments(args);

        return notesCreateDialogFragment;
    }


    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    /*when the create button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_note_create_dialog, container, false);

        titleEditText = view.findViewById(R.id.titleEditText);
        subtitleEditText = view.findViewById(R.id.subtitleEditText);
        noteEditText = view.findViewById(R.id.noteEditText);
        dateTextView = view.findViewById(R.id.dateTextView);

        createImageView = view.findViewById(R.id.updateImageView);
        backImageView = view.findViewById(R.id.backImageView);


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

        //when the create image is pressed it creates a new row in the database
        createImageView.setOnClickListener(view12 -> {

            titleString = titleEditText.getText().toString();
            subtitlleString = subtitleEditText.getText().toString();
            noteString = noteEditText.getText().toString();
            dateString = dateTextView.getText().toString();

            Notes notes = new Notes(-1, titleString, subtitlleString, noteString, dateString);

            DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(getContext());

            long id = databaseQueryClass.insertNote(notes);

            if(id>0){
                notes.setId(id);
                notesCreateListener.onNoteCreated(notes);
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


}