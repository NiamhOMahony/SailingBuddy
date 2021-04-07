package com.niamh.sailingbuddy.SessionCRUD.ShowSessionList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.Session;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.SessionCreateDialogFragment;
import com.niamh.sailingbuddy.SessionCRUD.CreateSession.SessionCreateListener;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class SessionListActivity2 extends AppCompatActivity implements SessionCreateListener {



    private DatabaseQueryClass databaseQueryClass;
    SharedPreferences sharedpreferences;
    String IdK = "ID_KEY";

    TextView sessionTextView;
    EditText inputSearch;

    CharSequence search = "";

    CharSequence name;
    String type;

    SwipeRefreshLayout recyclerSwipeRefresh;

    public List<Session> sessionList;

    //Passing information to the main content activity
    private TextView sessionEmptyListTextView;
    private SessionRecyclerViewAdapter2 sessionRecyclerViewAdapter;

    public RecyclerView recyclerView;

    //When class is created the following attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list2);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recyclerView = findViewById(R.id.sessionRecyclerView);

        sessionEmptyListTextView = findViewById(R.id.sessionEmptyListTextView);
        sessionTextView = findViewById(R.id.sessionTextView);

        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.setHint("Search Session Plans by Level");

        recyclerSwipeRefresh = findViewById(R.id.recyclerSwipeRefresh);
        LinearLayout layoutSearch = findViewById(R.id.layoutSearch);
        ImageView searchImage = findViewById(R.id.searchImage);

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

        // when bin view selected were are given the option to delete all
        ImageView deleteImageView = findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(v -> onDeleteAllSelected());

        //float action button brings us to the create fragment
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openSessionCreateDialog());

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

        //Swipe to refresh recyclerviews -
        // How to Implement Swipe Refresh in Android Studio | SwipeRefresh | Android Coding https://www.youtube.com/watch?v=NHEoMUPLTRU
        recyclerSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshData();

                recyclerSwipeRefresh.setRefreshing(false);
            }
        });

        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sessionRecyclerViewAdapter.getFilterByLevel().filter(s);

                if(type.equals("Instructor")){
                    sessionRecyclerViewAdapter.getFilterByName().filter(name);
                }

                search = s;
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //delete all fragment
    private void onDeleteAllSelected() {
        //When delete menu action is clicked the program asks if you're sure
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete all Session Plans?");
        //If you select yes after the warning the table clears
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    boolean isAllDeleted = databaseQueryClass.deleteAllSessions();
                    if(isAllDeleted){
                        sessionList.clear();
                        sessionRecyclerViewAdapter.notifyDataSetChanged();
                        viewVisibility();
                    }
                });

        //If you select no you're brought back to the "Homepage"
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void viewVisibility() {
        if(sessionList.isEmpty())
            sessionEmptyListTextView.setVisibility(View.VISIBLE);
        else
            sessionEmptyListTextView.setVisibility(View.GONE);
    }

    private void openSessionCreateDialog() {
        SessionCreateDialogFragment sessionCreateDialogFragment = SessionCreateDialogFragment.newInstance("CREATE SESSION", this);
        sessionCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_SESSION);
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
        sessionRecyclerViewAdapter = new SessionRecyclerViewAdapter2(SessionListActivity2.this, sessionList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(sessionRecyclerViewAdapter);

        if(type.equals("Instructor")){
            sessionRecyclerViewAdapter.getFilterByName().filter(name);
        }

    }

}