package com.niamh.sailingbuddy.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

//Calender app inspiration form - Calendar App Example Android Studio Tutorial https://www.youtube.com/watch?v=Ba0Q-cK1fJo&list=PLnQbggnVfvo0EkKf18MRHxsCCka0P8FTE&index=8

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener
        {
private TextView monthYearText;
private RecyclerView calendarRecyclerView;
private LocalDate selectedDate;

@Override
protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        initWidgets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        selectedDate = LocalDate.now();
        }
        setMonthView();

        /*Code adapted from How to Implement Bottom Navigation With Activities in Android Studio
         | BottomNav | Android Coding https://www.youtube.com/watch?v=JjfSjMs0ImQ*/
                BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
                bottomNavigationView.setSelectedItemId(R.id.menuHome);
                bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
                        switch (item.getItemId()){
                                case R.id.menuSettings:
                                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                                        overridePendingTransition(0,0);
                                        return true;
                                case R.id.menuHome:
                                        startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                                        overridePendingTransition(0,0);
                                        return true;
                                case R.id.menuUser:
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                        overridePendingTransition(0,0);
                                        return true;
                        }
                        return false;
                });
        }

private void initWidgets()
        {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        }

private void setMonthView()
        {
        monthYearText.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        }

private ArrayList<String> daysInMonthArray(LocalDate date)
        {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        yearMonth = YearMonth.from(date);
        }

        int daysInMonth = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        daysInMonth = yearMonth.lengthOfMonth();
        }

        LocalDate firstOfMonth = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        firstOfMonth = selectedDate.withDayOfMonth(1);
        }
        int dayOfWeek = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        }

        for(int i = 1; i <= 42; i++)
        {
        if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
        {
        daysInMonthArray.add("");
        }
        else
        {
        daysInMonthArray.add(String.valueOf(i - dayOfWeek));
        }
        }
        return  daysInMonthArray;
        }

private String monthYearFromDate(LocalDate date)
        {
        DateTimeFormatter formatter = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        return date.format(formatter);
        }else return null;
        }

public void previousMonthAction(View view)
        {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        selectedDate = selectedDate.minusMonths(1);
        }
        setMonthView();
        }

public void nextMonthAction(View view)
        {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        selectedDate = selectedDate.plusMonths(1);
        }
        setMonthView();
        }

@Override
public void onItemClick(int position, String dayText)
        {
        if(!dayText.equals(""))
        {
        String message = "Selected Date " + dayText + " " + monthYearFromDate(selectedDate);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
        }
        }