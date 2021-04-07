package com.niamh.sailingbuddy.NoteCRUD.ShowNoteList;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.NoteCRUD.CreateNote.Notes;
import com.niamh.sailingbuddy.NoteCRUD.UpdateNote.NotesUpdateDialogFragment;
import com.niamh.sailingbuddy.NoteCRUD.UpdateNote.NotesUpdateListener;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.Utils.Config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

public class NotesListRecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    //declaring and assigning values
    private Context context;
    private List<Notes> notesList;
    private List<Notes> notesFilteredList;
    private DatabaseQueryClass databaseQueryClass;

    public NotesListRecyclerViewAdapter(Context context, List<Notes> notesList) {
        this.context = context;
        this.notesList = notesList;
        this.notesFilteredList = notesList;
        databaseQueryClass = new DatabaseQueryClass(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Notes notes = notesFilteredList.get(position);

        holder.titleTextView.setText(notes.getTitle());
        holder.subtitleTextView.setText(notes.getSubtitle());
        holder.noteTextView.setText(notes.getNote());
        holder.dateTextView.setText(notes.getDateTime());


        //When the bin image is clicked it delets the attibute
        holder.binButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this note?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteNote(itemPosition);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



        //clicking the note will bring you to the update tab
        holder.noteHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotesUpdateDialogFragment notesUpdateDialogFragment = NotesUpdateDialogFragment.newInstance(notes.getId(), itemPosition, new NotesUpdateListener() {
                    @Override
                    public void onNoteInfoUpdate(Notes notes, int position) {
                        notesList.set(position, notes);
                        notifyDataSetChanged();
                    }
                });
                notesUpdateDialogFragment.show(((NotesListActivity) context).getSupportFragmentManager(), Config.UPDATE_NOTES);
            }
        });

    }

    //Deletes them based on ID no
    private void deleteNote(int position) {
        Notes notes = notesList.get(position);
        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(context);
        boolean isDeleted = databaseQueryClass.deleteNoteById(notes.getId());

        if(isDeleted) {
            notesList.remove(notes);
            notifyDataSetChanged();
            ((NotesListActivity) context).viewVisibility();
        } else
            Toast.makeText(context, "Cannot delete!", Toast.LENGTH_SHORT).show();
    }

    //count how man
    @Override
    public int getItemCount() {
        return notesFilteredList.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getFilterByNote() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    notesFilteredList = notesList;
                } else {
                    List<Notes> lstFiltered = new ArrayList<>();
                    for (Notes row : notesFilteredList) {
                        if (row.getNote().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    notesFilteredList = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = notesFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                notesFilteredList = (List<Notes>) results.values;
                notifyDataSetChanged();
            }
        };
    }



}
