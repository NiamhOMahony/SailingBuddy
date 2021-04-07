package com.niamh.sailingbuddy.UserCRUD.ShowUserList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList.SafetyListFilter;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.UpdateUser.UserUpdateDialogFragment;
import com.niamh.sailingbuddy.UserCRUD.UpdateUser.UserUpdateListener;
import com.niamh.sailingbuddy.Utils.Config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class UserListRecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolderUser>{

    //declaring and assigning values
    private final Context context;
    private final List<User> userList;
    private List<User> userFilteredData;

    String admin = "Admin:";
    String instructor = "Instructor:";

    SharedPreferences sharedPreferences;

    private final boolean expanded = true;

    public UserListRecyclerViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        this.userFilteredData =userList;

    }

    @Override
    public CustomViewHolderUser onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new CustomViewHolderUser(view);

    }

    @Override
    public void onBindViewHolder(CustomViewHolderUser holder, int position) {
        final int itemPosition = position;
        final User user = userFilteredData.get(position);


        String fullName = user.getName();
        holder.nameTextView.setText(fullName);
        holder.itemImageView.setImageBitmap(user.getImage());

        //clicking the holder will bring you to the update fragment used to be image of the pencil
        holder.userHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserUpdateDialogFragment userUpdateDialogFragment = UserUpdateDialogFragment.newInstance(user.getID(), itemPosition, new UserUpdateListener() {
                    @Override
                    public void onUserInfoUpdate(User user, int position) {
                        userList.set(position, user);
                        notifyDataSetChanged();
                    }
                });
                userUpdateDialogFragment.show(((UserListActivity) context).getSupportFragmentManager(), Config.UPDATE_USER);
            }
        });

        //When the bin image is clicked it deletes the attitude
        holder.binButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this user?");
                alertDialogBuilder.setPositiveButton("Yes",
                        (arg0, arg1) -> UserListRecyclerViewAdapter.this.deleteUser(itemPosition));

                alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    //Deletes them based on ID no
    private void deleteUser(int position) {
        User user = userList.get(position);
        DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(context);
        boolean isDeleted = databaseQueryClass.deleteUserById(user.getID());

        if (isDeleted) {
            userList.remove(user);
            notifyDataSetChanged();
            ((UserListActivity) context).viewVisibility();
        } else
            Toast.makeText(context, "Cannot delete!", Toast.LENGTH_SHORT).show();
    }

    //count how many
    @Override
    public int getItemCount() {
        return userFilteredData.size();
    }

    //RecyclerView(AndroidX 2020): Part 5 | Search bar with RecyclerView | Android Studio Tutorial https://www.youtube.com/watch?v=ILYfvCrpsj8
    //Getting a filter setup for search bar
    public Filter getTypeFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    userFilteredData = userList;
                } else {
                    List<User> lstFiltered = new ArrayList<>();
                    for (User row : userFilteredData) {
                        if (row.getType().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    userFilteredData = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userFilteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userFilteredData = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public Filter getNameFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String Key = constraint.toString();
                if (Key.isEmpty()) {
                    userFilteredData = userList;
                } else {
                    List<User> lstFiltered = new ArrayList<>();
                    for (User row : userFilteredData) {
                        if (row.getName().toLowerCase().contains(Key.toLowerCase())) {
                            lstFiltered.add(row);
                        }
                    }
                    userFilteredData = lstFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = userFilteredData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userFilteredData = (List<User>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
