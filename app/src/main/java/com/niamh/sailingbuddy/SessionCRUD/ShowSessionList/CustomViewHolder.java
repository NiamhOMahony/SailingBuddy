package com.niamh.sailingbuddy.SessionCRUD.ShowSessionList;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.niamh.sailingbuddy.R;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    TextView nameTextView;
    TextView levelTextView;
    TextView noStudentsTextView;
    TextView landTextView;
    TextView waterTextView;
    TextView areaTextView;
    TextView dateTextView;
    TextView highTextView;
    TextView lowTextView;
    TextView launchTextView;
    TextView recoveryTextView;
    TextView weatherTextView;
    ConstraintLayout sessionHolder;

    public CustomViewHolder (View itemView) {
        super(itemView);

        nameTextView = itemView.findViewById(R.id.instructorNameTextView);
        dateTextView = itemView.findViewById(R.id.dateTextView);
        levelTextView = itemView.findViewById(R.id.levelTextView);
        noStudentsTextView = itemView.findViewById(R.id.noStudentsTextView);
        landTextView = itemView.findViewById(R.id.landTextView);
        waterTextView = itemView.findViewById(R.id.waterTextView);
        areaTextView = itemView.findViewById(R.id.areaTextView);
        highTextView = itemView.findViewById(R.id.highTextView);
        lowTextView = itemView.findViewById(R.id.lowTextView);
        launchTextView = itemView.findViewById(R.id.launchTextView);
        recoveryTextView = itemView.findViewById(R.id.recoveryTextView);
        weatherTextView = itemView.findViewById(R.id.weatherTextView);
        sessionHolder = itemView.findViewById(R.id.sessionHolder);
    }
}
