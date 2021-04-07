package com.niamh.sailingbuddy.SessionCRUD.ShowSessionList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.R;

public class CustomViewHolder2 extends RecyclerView.ViewHolder {

    TextView nameTextView;
    TextView levelTextView;
    TextView noStudentsTextView;
    TextView dateTextView;
    ImageView crossImageView;
    ConstraintLayout sessionHolder;

    public CustomViewHolder2 (View itemView) {
        super(itemView);


        nameTextView = itemView.findViewById(R.id.instructorNameTextView);
        levelTextView = itemView.findViewById(R.id.levelTextView);
        noStudentsTextView = itemView.findViewById(R.id.noStudentsTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        crossImageView = itemView.findViewById(R.id.crossImageView);
        sessionHolder = itemView.findViewById(R.id.sessionHolder);

    }
}
