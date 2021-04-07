package com.niamh.sailingbuddy.SailingCRUD.ShowSailingList;

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
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;
import com.niamh.sailingbuddy.SailingCRUD.UpdateSailing.SailingUpdateDialogFragment;
import com.niamh.sailingbuddy.SailingCRUD.UpdateSailing.SailingUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SailingListFilter extends RecyclerView.Adapter<CustomViewHolder>{


    //declaring and assigning values
    private final Context context;
    private final List<Sailing> sailingList;
    private List<Sailing> sailingFilteredData;

    String available = "Available:";
    String fault = "Fault:";


    private final boolean expanded = true;

    public SailingListFilter(Context context, List<Sailing> sailingList) {
        this.context = context;
        this.sailingList = sailingList;
        this.sailingFilteredData =sailingList;

    }

    @Override
    public @NotNull CustomViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_sailing, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final Sailing sailing = sailingFilteredData.get(position);

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
              SailingUpdateDialogFragment sailingUpdateDialogFragment = SailingUpdateDialogFragment.newInstance(sailing.getId(), new SailingUpdateListener() {
                    @Override
                    public void onSailingInfoUpdate(Sailing sailing) {
                        sailingList.set(position, sailing);
                        notifyDataSetChanged();
                    }
                });
                sailingUpdateDialogFragment.show(((SailingListActivity) context).getSupportFragmentManager(), Config.UPDATE_SAILING);
            }
        });

        //When the bin image is clicked it deletes the attitude
        holder.binButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this sailing equipment?");
                alertDialogBuilder.setPositiveButton("Yes",
                        (arg0, arg1) -> SailingListFilter.this.deleteSailing(itemPosition));

                alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });


    }

    //Deletes them based on ID no
    private void deleteSailing(int position) {
        Sailing sailing = sailingList.get(position);
        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(context);
        boolean isDeleted = databaseQueryClass.deleteSailingById(sailing.getId());

        if (isDeleted) {
            sailingList.remove(sailing);
            notifyDataSetChanged();
            ((SailingListActivity) context).viewVisibility();
        } else
            Toast.makeText(context, "Cannot delete!", Toast.LENGTH_SHORT).show();
    }

    //count how many
    @Override
    public int getItemCount() {
        return sailingFilteredData.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getAvailableFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    sailingFilteredData = sailingList;
                } else {
                    List<Sailing> lstFiltered = new ArrayList<>();
                    for (Sailing row : sailingFilteredData) {
                        if (row.getAvailable().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    sailingFilteredData = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = sailingFilteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sailingFilteredData = (List<Sailing>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    sailingFilteredData = sailingList;
                } else {
                    List<Sailing> lstFiltered = new ArrayList<>();
                    for (Sailing row : sailingFilteredData) {
                        if (row.getType().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    sailingFilteredData = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = sailingFilteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                sailingFilteredData = (List<Sailing>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
