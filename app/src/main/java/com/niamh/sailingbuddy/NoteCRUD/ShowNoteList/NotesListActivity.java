package com.niamh.sailingbuddy.NoteCRUD.ShowNoteList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.NoteCRUD.CreateNote.NotesCreateDialogFragment;
import com.niamh.sailingbuddy.NoteCRUD.CreateNote.NotesCreateListener;
import com.niamh.sailingbuddy.NoteCRUD.CreateNote.Notes;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.List;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

public class NotesListActivity extends AppCompatActivity implements NotesCreateListener {

    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);

    TextView sessionTextView;
    EditText inputSearch;

    CharSequence search = "";

    private List<Notes> notesList = new ArrayList<>();

    //Passing iformation to the main content activty
    private TextView notesListEmptyTextView;
    private RecyclerView recyclerView;
    private NotesListRecyclerViewAdapter notesListRecyclerViewAdapter;


    //When class is created the folowing attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ConstraintLayout fullLayout = (ConstraintLayout) this.findViewById(R.id.fullLayout);
        setContentView(R.layout.activity_note_list);

        // making the recycler slpit in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0

        recyclerView = findViewById(R.id.recyclerView);
        notesListEmptyTextView = findViewById(R.id.emptyListTextView);

        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.setHint("Search Notes");

        notesList.addAll(databaseQueryClass.getAllNotes() );

        notesListRecyclerViewAdapter = new NotesListRecyclerViewAdapter(this, notesList);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );
        recyclerView.setAdapter(notesListRecyclerViewAdapter);

        viewVisibility();

        ImageView backImageCiew = findViewById(R.id.backImageView);
        backImageCiew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // when bin view selected were are given the option to delete all
        ImageView deleteImageView = findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteAllSelected();
            }
        });


        //float action button brings us to the create fragment
        ImageView fab = findViewById(R.id.addImageView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNoteCreateDialog();
            }
        });

        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesListRecyclerViewAdapter.getFilterByNote().filter(s);

                search = s;
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

         /*Code adapted from How to Implement Bottom Navigation With Activities in Android Studio
         | BottomNav | Android Coding https://www.youtube.com/watch?v=JjfSjMs0ImQ*/
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.menuHome);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menuSettings:
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHome:
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuUser:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });

    }


    //delete all fragment
    private void onDeleteAllSelected() {
        //When delete menu action is clicked the program asks if youre sure
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete all Notes?");
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    //If you select yes after the warning the table clears
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        boolean isAllDeleted = databaseQueryClass.deleteAllNotes();
                        if(isAllDeleted){
                            notesList.clear();
                            notesListRecyclerViewAdapter.notifyDataSetChanged();
                            viewVisibility();
                        }
                    }
                });

        //If you select no you're brought back to the "Homepage"
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    public void viewVisibility() {
        if(notesList.isEmpty())
            notesListEmptyTextView.setVisibility(View.VISIBLE);
        else
            notesListEmptyTextView.setVisibility(View.GONE);
    }

    private void openNoteCreateDialog() {
        NotesCreateDialogFragment notesCreateDialogFragment = NotesCreateDialogFragment.newInstance("Create Note", this);
        notesCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_NOTES);
    }

    //when new data is added update main recycler view
    @Override
    public void onNoteCreated(Notes notes) {
        notesList.add(notes);
        notesListRecyclerViewAdapter.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_IS4447***","Update RecyclerView: "+ notes.getTitle());
    }

}