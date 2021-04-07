package com.niamh.sailingbuddy.SessionCRUD.ShowSessionList;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.SessionCRUD.UpdateSession.SessionUpdateDialogFragment;
import com.niamh.sailingbuddy.SessionCRUD.UpdateSession.SessionUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.List;

public class SessionRecyclerViewAdapter2 extends RecyclerView.Adapter<CustomViewHolder2> {

    //declaring and assigning values
    private Context context;
    private List<Session> sessionList;
    private List<Session> sessionFilteredList;
    private DatabaseQueryClass databaseQueryClass;

    public SessionRecyclerViewAdapter2(Context context, List<Session> sessionList) {
        this.context = context;
        this.sessionList = sessionList;
        this.sessionFilteredList = sessionList;
        databaseQueryClass = new DatabaseQueryClass(context);
    }

    @Override
    public CustomViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session_2, parent, false);
        return new CustomViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder2 holder, int position) {
        final int itemPosition = position;
        final Session session = sessionFilteredList.get(position);

        holder.nameTextView.setText(session.getInstructorName());
        holder.levelTextView.setText(session.getLevel());
        holder.noStudentsTextView.setText(session.getNoStudents());
        holder.dateTextView.setText(session.getDate());


        //When the bin image is clicked it delets the attibute
        holder.crossImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this session plan?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteSession(itemPosition);
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



        //clicking the session plan will bring you to the update tab
        holder.sessionHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionUpdateDialogFragment sessionUpdateDialogFragment = SessionUpdateDialogFragment.newInstance(session.getId(), itemPosition, new SessionUpdateListener() {
                    @Override
                    public void onSessionInfoUpdate(Session session, int position) {
                        sessionList.set(position, session);
                        notifyDataSetChanged();
                    }
                });
                sessionUpdateDialogFragment.show(((SessionListActivity2) context).getSupportFragmentManager(), Config.UPDATE_SESSION);
            }
        });

    }

    //Deletes them based on ID no
    private void deleteSession(int position) {
        Session session = sessionList.get(position);
        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(context);
        boolean isDeleted = databaseQueryClass.deleteSessionById(session.getId());

        if(isDeleted) {
            sessionList.remove(session);
            notifyDataSetChanged();
            ((SessionListActivity2) context).viewVisibility();
        } else
            Toast.makeText(context, "Cannot delete!", Toast.LENGTH_SHORT).show();
    }

    //count how man
    @Override
    public int getItemCount() {
        return sessionFilteredList.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getFilterByName() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    sessionFilteredList = sessionList;
                } else {
                    List<Session> lstFiltered = new ArrayList<>();
                    for (Session row : sessionFilteredList) {
                        if (row.getInstructorName().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    sessionFilteredList = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = sessionFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sessionFilteredList = (List<Session>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public Filter getFilterByLevel() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    sessionFilteredList = sessionList;
                } else {
                    List<Session> lstFiltered = new ArrayList<>();
                    for (Session row : sessionFilteredList) {
                        if (row.getLevel().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    sessionFilteredList = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = sessionFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sessionFilteredList = (List<Session>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
