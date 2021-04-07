package com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.Safety;
import com.niamh.sailingbuddy.SafetyCRUD.UpdateSafety.SafetyUpdateDialogFragment;
import com.niamh.sailingbuddy.SafetyCRUD.UpdateSafety.SafetyUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SafetyListFilter extends RecyclerView.Adapter<CustomViewHolder>{


    //declaring and assigning values
    private final Context context;
    private final List<Safety> safetyList;
    private List<Safety> safetyListFilter;

    String available = "Available:";
    String fault = "Fault:";

    int safetyId;


    private final boolean expanded = true;

    public SafetyListFilter(Context context, List<Safety> safetyList) {
        this.context = context;
        this.safetyList = safetyList;
        this.safetyListFilter =safetyList;

    }

    @Override
    public @NotNull CustomViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_safety, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Safety safety = safetyListFilter.get(position);

        holder.typeTextView.setText(safety.getType());
        holder.availableTextView.setText(safety.getAvailable());
        holder.faultTextView.setText(safety.getFault());
        holder.itemImageView.setImageBitmap(safety.getImage());
        holder.availableTitleTextView.setText(available);
        holder.faultTitleTextView.setText(fault);

        //clicking the holder will bring you to the update fragment used to be image of the pencil
        holder.safetyHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SafetyUpdateDialogFragment safetyUpdateDialogFragment = SafetyUpdateDialogFragment.newInstance(safety.getId(), new SafetyUpdateListener() {
                    @Override
                    public void onSafetyInfoUpdate(Safety safety) {
                        safetyId = (int) safety.getId();
                        safetyList.set(position, safety);
                        notifyDataSetChanged();
                    }
                });
                safetyUpdateDialogFragment.show(((SafetyListActivity) context).getSupportFragmentManager(), Config.UPDATE_SAFETY);
            }
        });

        //When the bin image is clicked it deletes the attitude
        holder.binButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this safety equipment?");
                alertDialogBuilder.setPositiveButton("Yes",
                        (arg0, arg1) -> SafetyListFilter.this.deleteSafety(itemPosition));

                alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    //Deletes them based on ID no
    private void deleteSafety(int position) {
        Safety safety = safetyList.get(position);
        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(context);
        boolean isDeleted = databaseQueryClass.deleteSafetyById(safety.getId());

        if (isDeleted) {
            safetyList.remove(safety);
            notifyDataSetChanged();
            ((SafetyListActivity) context).viewVisibility();
        } else
            Toast.makeText(context, "Cannot delete!", Toast.LENGTH_SHORT).show();
    }

    //count how many
    @Override
    public int getItemCount() {
        return safetyListFilter.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getAvailableFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    safetyListFilter = safetyList;
                } else {
                    List<Safety> lstFiltered = new ArrayList<>();
                    for (Safety row : safetyListFilter) {
                        if (row.getAvailable().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    safetyListFilter = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = safetyListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                safetyListFilter = (List<Safety>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    //Getting a filter setup for search bar
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    safetyListFilter = safetyList;
                } else {
                    List<Safety> lstFiltered = new ArrayList<>();
                    for (Safety row : safetyListFilter) {
                        if (row.getType().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    safetyListFilter = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = safetyListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                safetyListFilter = (List<Safety>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
