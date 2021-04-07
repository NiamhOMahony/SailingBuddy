package com.niamh.sailingbuddy.SailingCRUD.ShowSailingList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;
import com.niamh.sailingbuddy.SailingCRUD.UpdateSailing.SailingUpdateDialogFragment;
import com.niamh.sailingbuddy.SailingCRUD.UpdateSailing.SailingUpdateDialogFragment2;
import com.niamh.sailingbuddy.SailingCRUD.UpdateSailing.SailingUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SailingListFilter2 extends RecyclerView.Adapter<CustomViewHolder>{


    //declaring and assigning values
    private final Context context;
    private final List<Sailing> sailingList2;
    private List<Sailing> sailingFilteredData2;

    String available = "Available:";
    String fault = "Fault:";


    private final boolean expanded = true;

    public SailingListFilter2(Context context, List<Sailing> sailingList2) {
        this.context = context;
        this.sailingList2 = sailingList2;
        this.sailingFilteredData2 =sailingList2;

    }

    @Override
    public @NotNull CustomViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sailing2, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Sailing sailing = sailingFilteredData2.get(position);

        holder.typeTextView.setText(sailing.getType());
        holder.availableTextView.setText(sailing.getAvailable());
        holder.faultTextView.setText(sailing.getFault());
        holder.itemImageView.setImageBitmap(sailing.getImage());
        holder.availableTitleTextView.setText(available);
        holder.faultTitleTextView.setText(fault);

        //clicking the holder will bring you to the update fragment used to be image of the pencil
        holder.sailingHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SailingUpdateDialogFragment2 sailingUpdateDialogFragment2 = SailingUpdateDialogFragment2.newInstance(sailing.getId(), new SailingUpdateListener() {
                    @Override
                    public void onSailingInfoUpdate(Sailing sailing) {
                        sailingList2.set(position, sailing);
                        notifyDataSetChanged();
                    }
                });
                sailingUpdateDialogFragment2.show(((SailingListActivity2) context).getSupportFragmentManager(), Config.UPDATE_SAILING);
            }
        });


    }

    //count how many
    @Override
    public int getItemCount() {
        return sailingFilteredData2.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getAvailableFilter2() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    sailingFilteredData2 = sailingList2;
                } else {
                    List<Sailing> lstFiltered = new ArrayList<>();
                    for (Sailing row : sailingFilteredData2) {
                        if (row.getAvailable().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    sailingFilteredData2 = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = sailingFilteredData2;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sailingFilteredData2 = (List<Sailing>) results.values;
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
                    sailingFilteredData2 = sailingList2;
                } else {
                    List<Sailing> lstFiltered = new ArrayList<>();
                    for (Sailing row : sailingFilteredData2) {
                        if (row.getType().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    sailingFilteredData2 = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = sailingFilteredData2;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sailingFilteredData2 = (List<Sailing>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
