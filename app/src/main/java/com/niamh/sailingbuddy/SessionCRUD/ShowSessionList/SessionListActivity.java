package com.niamh.sailingbuddy.SessionCRUD.ShowSessionList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.niamh.sailingbuddy.Calendar.CalendarActivity;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.Paint.PaintActivity;
import com.niamh.sailingbuddy.NoteCRUD.ShowNoteList.NotesListActivity;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.SessionCreateListener;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class SessionListActivity extends AppCompatActivity implements SessionCreateListener {



    private DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);

    ConstraintLayout sessionHeader;
    TextView sessionTextView;

    TextView tipsTextView;

    ImageView starTextView, diceTextView, gamesTextView, curriculumTextView, exerciseTextView;

    TextView toolsTextView;
    ImageView paintImageView, notesImageView, calenderImageView;

    BottomNavigationView bottomNav;

    CharSequence name;
    String type;

    SwipeRefreshLayout recyclerSwipeRefresh;

    public List<Session> sessionList;
    private boolean ascending = true;

    //Passing information to the main content activity
    private TextView sessionEmptyListTextView;
    private SessionRecyclerViewAdapter sessionRecyclerViewAdapter;

    public RecyclerView recyclerView;

    SharedPreferences sharedpreferences;
    String IdK = "ID_KEY";

    //When class is created the following attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        sessionEmptyListTextView = findViewById(R.id.emptyListTextView);
        sessionTextView = findViewById(R.id.sessionTextView);

        recyclerSwipeRefresh = findViewById(R.id.recyclerSwipeRefresh);

        sessionTextView = findViewById(R.id.sessionTextView);
        recyclerView = findViewById(R.id.sessionRecyclerView);
        tipsTextView = findViewById(R.id.tipsTextView);
        starTextView = findViewById(R.id.starTextView);
        diceTextView = findViewById(R.id.diceTextView);
        gamesTextView = findViewById(R.id.gamesTextView);
        curriculumTextView = findViewById(R.id.curriculumTextView);
        exerciseTextView = findViewById(R.id.exerciseTextView);
        toolsTextView = findViewById(R.id.toolsTextView);
        paintImageView = findViewById(R.id.paintImageView);
        notesImageView = findViewById(R.id.notesImageView);
        calenderImageView = findViewById(R.id.calenderImageView);
        bottomNav = findViewById(R.id.bottomNav);

        databaseQueryClass = new DatabaseQueryClass(this);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        int Id = sharedpreferences.getInt(IdK,1);
        User mUser = databaseQueryClass.getUserById(Id);

        name = mUser.getName();
        type = mUser.getType();

        refreshData();

        viewVisibility();

        //Sorting RecyclerView by Date inspiration from - https://stackoverflow.com/questions/50562960/sort-recyclerview-by-date
        Collections.sort(sessionList, new Comparator<Session>() {
            @Override
            public int compare(Session o1, Session o2) {
                String Date = o1.getDate();
                String Date1 = o2.getDate();

                return Date.compareTo(Date1);
            }
        });

        Collections.reverse(sessionList);


        //float action button brings us to the create fragment
        ImageView fab = findViewById(R.id.addImageView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sessionIntent = new Intent(SessionListActivity.this, SessionListActivity2.class);
                startActivity(sessionIntent);
            }
        });

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
        notesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notesIntent = new Intent(SessionListActivity.this, NotesListActivity.class);
                startActivity(notesIntent);
            }
        });

        paintImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent paintIntent = new Intent(SessionListActivity.this, PaintActivity.class);
                startActivity(paintIntent);
            }
        });

        calenderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calendarIntent = new Intent(SessionListActivity.this, CalendarActivity.class);
                startActivity(calendarIntent);
            }
        });

        starTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.sailing.ie/Training/Courses/Dinghy-Keelboat-Catamaran-Sailing");
            }
        });

        diceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.sailing.ie/Training/Courses/Dinghy-Keelboat-Catamaran-Sailing");
            }
        });

        curriculumTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.sailing.ie/Training/Courses/Dinghy-Keelboat-Catamaran-Sailing");
            }
        });

        gamesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.sailing.ie/Training/Courses/Dinghy-Keelboat-Catamaran-Sailing");
            }
        });

        exerciseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.sailing.ie/Training/Courses/Dinghy-Keelboat-Catamaran-Sailing");
            }
        });

        //Swipe to refresh recyclerviews -
        // How to Implement Swipe Refresh in Android Studio | SwipeRefresh | Android Coding https://www.youtube.com/watch?v=NHEoMUPLTRU
        recyclerSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshData();

                recyclerSwipeRefresh.setRefreshing(false);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void viewVisibility() {
        if(sessionList.isEmpty())
            sessionEmptyListTextView.setVisibility(View.VISIBLE);
        else
            sessionEmptyListTextView.setVisibility(View.GONE);
    }


    public void onSessionInfoUpdate(Session session){
        sessionRecyclerViewAdapter.notifyDataSetChanged();

        viewVisibility();
        Log.d("***NIAMH_FYP***","Update Safety Equip: "+session.getInstructorName());
    }

    //when new data is added update main recycler view
    @Override
    public void onSessionCreated(Session session) {
        sessionList.add(session);
        sessionRecyclerViewAdapter.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_FYP***","Create Safety Equip: "+session.getInstructorName());
    }

    public void refreshData(){
        sessionList = new ArrayList<>();
        sessionList.addAll(databaseQueryClass.getAllSession());

        // making the recycler split in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0
        sessionRecyclerViewAdapter = new SessionRecyclerViewAdapter(SessionListActivity.this, sessionList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(sessionRecyclerViewAdapter);


        if(type.equals("Instructor")){
            sessionRecyclerViewAdapter.getFilterByName().filter(name);
        }

    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


}
