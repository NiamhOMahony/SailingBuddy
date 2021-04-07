package com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.R;


public class CustomViewHolder extends RecyclerView.ViewHolder {

    //Declaring Variables
    TextView typeTextView;
    TextView availableTextView;
    TextView availableTitleTextView;
    TextView faultTextView;
    TextView faultTitleTextView;
    ImageView itemImageView;
    ImageView binButtonImageView;
    ConstraintLayout safetyHolder;


    public CustomViewHolder(View itemView){
        super(itemView);

        //Declaring where to place the values
        typeTextView = itemView.findViewById(R.id.typeTextView);
        availableTextView = itemView.findViewById(R.id.availableTextView);
        faultTextView = itemView.findViewById(R.id.faultTextView);
        itemImageView = itemView.findViewById(R.id.itemImageView);
        availableTitleTextView = itemView.findViewById(R.id.availableTitleTextView);
        faultTitleTextView = itemView.findViewById(R.id.faultTitleTextView);
        binButtonImageView = itemView.findViewById(R.id.crossImageView);
        safetyHolder = itemView.findViewById(R.id.safetyHolder);

    }
}
