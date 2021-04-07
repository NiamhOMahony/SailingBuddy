package com.niamh.sailingbuddy.SessionCRUD.ShowSessionList;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.SessionCRUD.UpdateSession.SessionUpdateDialogFragment;
import com.niamh.sailingbuddy.SessionCRUD.UpdateSession.SessionUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SessionRecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    //declaring and assigning values
    private Context context;
    private List<Session> sessionList;
    private List<Session> sessionFilteredList;
    private DatabaseQueryClass databaseQueryClass;

    public SessionRecyclerViewAdapter(Context context, List<Session> sessionList) {
        this.context = context;
        this.sessionList = sessionList;
        this.sessionFilteredList = sessionList;
        databaseQueryClass = new DatabaseQueryClass(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_session, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Session session = sessionFilteredList.get(position);

        holder.nameTextView.setText(session.getInstructorName());
        holder.levelTextView.setText(session.getLevel());
        holder.noStudentsTextView.setText(session.getNoStudents());
        holder.dateTextView.setText(session.getDate());
        holder.landTextView.setText(session.getLandActvity());
        holder.launchTextView.setText(session.getLaunchTime());
        holder.recoveryTextView.setText(session.getRecoveryTime());
        holder.waterTextView.setText(session.getWaterActivity());
        holder.areaTextView.setText(session.getSailArea());
        holder.highTextView.setText(session.getHighTide());
        holder.lowTextView.setText(session.getLowTide());
        holder.weatherTextView.setText(session.getWeather());

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
                sessionUpdateDialogFragment.show(((SessionListActivity) context).getSupportFragmentManager(), Config.UPDATE_SESSION);
            }
        });

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