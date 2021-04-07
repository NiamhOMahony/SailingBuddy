package com.niamh.sailingbuddy.NoteCRUD.UpdateNote;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.NoteCRUD.CreateNote.Notes;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.Calendar;

/*
 * Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io
 */

public class NotesUpdateDialogFragment extends DialogFragment {
    //Updating existing Notes

    //Decalring variables
    private static NotesUpdateListener notesUpdateListener;
    private static long noteId;
    private static int position;

    private Notes mNotes;

    private EditText titleEditText;
    private EditText subtitleEditText;
    private EditText noteEditText;
    private TextView dateTextView;

    private ImageView updateImageView;
    private ImageView backImageView;

    //givig variables values
    private String titleString = "";
    private String subtitlleString = "";
    private String noteString = "";
    private String dateString ="";

    private DatabaseQueryClass databaseQueryClass;

    private DatePickerDialog datePickerDialog;

    public NotesUpdateDialogFragment() {
        // Required empty public constructor
    }

    //when a new instance of the create fragment is opened it processes the below
    public static NotesUpdateDialogFragment newInstance(long subId, int pos, NotesUpdateListener listener){
        //added difference of declaring 3 new variables
        noteId = subId;
        position = pos;
        notesUpdateListener = listener;

        NotesUpdateDialogFragment notesUpdateDialogFragment = new NotesUpdateDialogFragment();
        Bundle args = new Bundle();
        notesUpdateDialogFragment.setArguments(args);
        return notesUpdateDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_update_dialog, container, false);

        /*when the update button is clicked whatever has been written into the edit text boxes is gotten and declared
     as a new row in the database*/

        titleEditText = view.findViewById(R.id.titleEditText);
        subtitleEditText = view.findViewById(R.id.subtitleEditText);
        noteEditText = view.findViewById(R.id.noteEditText);
        dateTextView = view.findViewById(R.id.dateTextView);

        updateImageView = view.findViewById(R.id.updateImageView);
        backImageView = view.findViewById(R.id.backImageView);

        databaseQueryClass = new DatabaseQueryClass(getContext());

        String title = getArguments().getString(Config.TITLE);
        getDialog().setTitle(title);

        mNotes = databaseQueryClass.getNoteById(noteId);
        //instead of getting the values like in create wer setting the values to our updated
        titleEditText.setText(mNotes.getTitle());
        subtitleEditText.setText(mNotes.getSubtitle());
        noteEditText.setText(mNotes.getNote());
        dateTextView.setText(String.valueOf(mNotes.getDateTime()));



        updateImageView.setOnClickListener(new View.OnClickListener() {
            //when we return to view we will get our new values
            @Override
            public void onClick(View view) {
                titleString  = titleEditText.getText().toString();
                subtitlleString = subtitleEditText.getText().toString();
                noteString  = noteEditText.getText().toString();
                dateString = dateTextView.getText().toString();

                mNotes.setTitle(titleString);
                mNotes.setSubtitle(subtitlleString);
                mNotes.setNote(noteString);
                mNotes.setDateTime(dateString);

                long id = databaseQueryClass.updateNote(mNotes);

                if(id>0){
                    notesUpdateListener.onNoteInfoUpdate(mNotes, position);
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

}


