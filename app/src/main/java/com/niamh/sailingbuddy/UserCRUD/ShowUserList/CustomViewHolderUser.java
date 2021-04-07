package com.niamh.sailingbuddy.UserCRUD.ShowUserList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.niamh.sailingbuddy.R;

public class CustomViewHolderUser extends RecyclerView.ViewHolder {

    //Declaring Variables
    TextView nameTextView;
    ImageView itemImageView;
    ImageView binButtonImageView;
    ConstraintLayout userHolder;
    SharedPreferences sharedPreferences;


    public CustomViewHolderUser(View itemView) {
        super(itemView);

        //Declaring where to place the values
        nameTextView = itemView.findViewById(R.id.nameTextView);
        itemImageView = itemView.findViewById(R.id.userImageView);
        binButtonImageView = itemView.findViewById(R.id.crossImageView);
        userHolder = itemView.findViewById(R.id.userHolder);

    }
}