package com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.Safety;
import com.niamh.sailingbuddy.SafetyCRUD.UpdateSafety.SafetyUpdateDialogFragment;
import com.niamh.sailingbuddy.SafetyCRUD.UpdateSafety.SafetyUpdateDialogFragment2;
import com.niamh.sailingbuddy.SafetyCRUD.UpdateSafety.SafetyUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SafetyListFilter2 extends RecyclerView.Adapter<CustomViewHolder>{


    //declaring and assigning values
    private final Context context;
    private final List<Safety> safetyList2;
    private List<Safety> safetyFilteredData2;

    String available = "Available:";
    String fault = "Fault:";


    private final boolean expanded = true;

    public SafetyListFilter2(Context context, List<Safety> safetyList2) {
        this.context = context;
        this.safetyList2 = safetyList2;
        this.safetyFilteredData2 =safetyList2;

    }

    @Override
    public @NotNull CustomViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_safety2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Safety safety = safetyFilteredData2.get(position);

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
                SafetyUpdateDialogFragment2 safetyUpdateDialogFragment2 = SafetyUpdateDialogFragment2.newInstance(safety.getId(), new SafetyUpdateListener() {
                    @Override
                    public void onSafetyInfoUpdate(Safety safety) {
                        safetyList2.set(position, safety);
                        notifyDataSetChanged();
                    }
                });
                safetyUpdateDialogFragment2.show(((SafetyListActivity2) context).getSupportFragmentManager(), Config.UPDATE_SAFETY);
            }
        });


    }

    //count how many
    @Override
    public int getItemCount() {
        return safetyFilteredData2.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getAvailableFilter2() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    safetyFilteredData2 = safetyList2;
                } else {
                    List<Safety> lstFiltered = new ArrayList<>();
                    for (Safety row : safetyFilteredData2) {
                        if (row.getAvailable().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    safetyFilteredData2 = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = safetyFilteredData2;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                safetyFilteredData2 = (List<Safety>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    //Getting a filter setup for search bar
    public Filter getFilter2() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    safetyFilteredData2 = safetyList2;
                } else {
                    List<Safety> lstFiltered = new ArrayList<>();
                    for (Safety row : safetyFilteredData2) {
                        if (row.getType().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    safetyFilteredData2 = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = safetyFilteredData2;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                safetyFilteredData2 = (List<Safety>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
