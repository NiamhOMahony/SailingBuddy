package com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

import android.content.Intent;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.Safety;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.SafetyCreateDialogFragment;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.SafetyCreateListener;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.List;

public class SafetyListActivity extends AppCompatActivity implements SafetyCreateListener {



    private final DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);

    EditText inputSearch;
    ImageView killcord;
    ImageView grabbag;
    ImageView vhf;
    ImageView firstaid;
    ImageView dinghy;
    ImageView anchor;
    ImageView bouy;

    LinearLayout layoutItems;
    TextView safetyEquipTextView;
    TextView all;
    CharSequence search = "";

    CharSequence KillCord = "killcord";
    CharSequence GrabBag = "grabbag";
    CharSequence VHF = "vhf";
    CharSequence FirstAid = "firstaid";
    CharSequence SafetyBoat = "safetyboat";
    CharSequence Anchor = "anchor";
    CharSequence Bouy = "bouy";
    CharSequence All = "";

    CharSequence Available = "Yes";
    CharSequence NotAvailable = "No";

    Animation anim_from_button, anim_from_top, anim_from_left, shakeAnimation;

    SwipeRefreshLayout recyclerSwipeRefresh;
    SwipeRefreshLayout availSwipeRefresh;

    public List<Safety> safetyList;

    //Passing information to the main content activity
    private TextView safetyListEmptyTextView;
    private SafetyListRecyclerViewAdapter safetyListRecyclerViewAdapter;
    private SafetyListFilter safetyListFilter;

    public RecyclerView recyclerView;
    public RecyclerView availableRecyclerView;

    //When class is created the folowing attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_list);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

         recyclerView = findViewById(R.id.recyclerView);
         availableRecyclerView = findViewById(R.id.availableRecyclerView);

        safetyListEmptyTextView = findViewById(R.id.emptyListTextView);
        layoutItems = findViewById(R.id.layoutItems);
        safetyEquipTextView = findViewById(R.id.safetyEquipTextView);

        killcord = findViewById(R.id.killcord);
        grabbag = findViewById(R.id.grabbag);
        vhf = findViewById(R.id.vhf);
        firstaid = findViewById(R.id.firstaid);
        dinghy = findViewById(R.id.dinghy);
        anchor = findViewById(R.id.anchor);
        bouy = findViewById(R.id.bouy);
        all = findViewById(R.id.all);

        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.setHint("Search Safety Equipment Descriptions");

        recyclerSwipeRefresh = findViewById(R.id.recyclerSwipeRefresh);
        availSwipeRefresh = findViewById(R.id.availSwipeRefresh);
        LinearLayout layoutSearch = findViewById(R.id.layoutSearch);
        ImageView searchImage = findViewById(R.id.searchImage);

        refreshData();

        viewVisibility();

        // when bin view selected were are given the option to delete all
        ImageView deleteImageView = findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(v -> onDeleteAllSelected());

        //float action button brings us to the create fragment
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openSafetyCreateDialog());

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

        availSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                availSwipeRefresh.setRefreshing(false);
            }
        });

        //Have to go back to all after each click must fix
        //When all is clicked all items in list show up
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshData();

                }
        });
        //When Killcord image is clicked all items in list that are type killcord show up
        killcord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    safetyListRecyclerViewAdapter.getFilter().filter(All);
                    safetyListRecyclerViewAdapter.getFilter().filter(KillCord);

                    safetyListFilter.getFilter().filter(All);
                    safetyListFilter.getFilter().filter(KillCord);

                    safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                    safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When GrabBag image is clicked all items in list that are type GrabBag show up
        grabbag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter.getFilter().filter(All);
                safetyListRecyclerViewAdapter.getFilter().filter(GrabBag);

                safetyListFilter.getFilter().filter(All);
                safetyListFilter.getFilter().filter(GrabBag);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When VHF image is clicked all items in list that are type VHF show up
        vhf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter.getFilter().filter(All);
                safetyListRecyclerViewAdapter.getFilter().filter(VHF);

                safetyListFilter.getFilter().filter(All);
                safetyListFilter.getFilter().filter(VHF);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When FirstAid image is clicked all items in list that are type FirstAid show up
        firstaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter.getFilter().filter(All);
                safetyListRecyclerViewAdapter.getFilter().filter(FirstAid);

                safetyListFilter.getFilter().filter(All);
                safetyListFilter.getFilter().filter(FirstAid);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When dinghy image is clicked all items in list that are type dinghy show up
        dinghy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter.getFilter().filter(All);
                safetyListRecyclerViewAdapter.getFilter().filter(SafetyBoat);

                safetyListFilter.getFilter().filter(All);
                safetyListFilter.getFilter().filter(SafetyBoat);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When anchor image is clicked all items in list that are type anchor show up
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter.getFilter().filter(All);
                safetyListRecyclerViewAdapter.getFilter().filter(Anchor);

                safetyListFilter.getFilter().filter(All);
                safetyListFilter.getFilter().filter(Anchor);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When bouy image is clicked all items in list that are type bouy show up
        bouy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter.getFilter().filter(All);
                safetyListRecyclerViewAdapter.getFilter().filter(Bouy);

                safetyListFilter.getFilter().filter(All);
                safetyListFilter.getFilter().filter(Bouy);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                safetyListRecyclerViewAdapter.getFilter().filter(s);
                safetyListFilter.getFilter().filter(s);

                safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                safetyListFilter.getAvailableFilter().filter(NotAvailable);
                search = s;
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(this, R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(this, R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(this, R.anim.anim_from_left);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        //Set Animations
        safetyEquipTextView.setAnimation(anim_from_top);
        deleteImageView.setAnimation(anim_from_top);
        layoutSearch.setAnimation(anim_from_top);
        inputSearch.setAnimation(anim_from_top);
        searchImage.setAnimation(anim_from_top);
        layoutItems.setAnimation(anim_from_left);
        recyclerView.setAnimation(anim_from_left);
        availableRecyclerView.setAnimation(anim_from_left);
        bottomNavigationView.setAnimation(anim_from_button);
        fab.setAnimation(anim_from_button);


    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //delete all fragment
    private void onDeleteAllSelected() {
        //When delete menu action is clicked the program asks if youre sure
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete all Safety Equipment?");
        //If you select yes after the warning the table clears
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    boolean isAllDeleted = databaseQueryClass.deleteAllSafety();
                    if(isAllDeleted){
                        safetyList.clear();
                       safetyListRecyclerViewAdapter.notifyDataSetChanged();
                        safetyListFilter.notifyDataSetChanged();
                        viewVisibility();
                    }
                });

        //If you select no you're brought back to the "Homepage"
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void viewVisibility() {
        if(safetyList.isEmpty())
            safetyListEmptyTextView.setVisibility(View.VISIBLE);
        else
            safetyListEmptyTextView.setVisibility(View.GONE);
    }

    private void openSafetyCreateDialog() {
        SafetyCreateDialogFragment safetyCreateDialogFragment = SafetyCreateDialogFragment.newInstance(this);
        safetyCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_SAFETY);
    }

    //when new data is added update main recycler view
    @Override
    public void onSafetyCreated(Safety safety) {
        safetyList.add(safety);
        safetyListRecyclerViewAdapter.notifyDataSetChanged();
        safetyListFilter.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_FYP***","Create Safety Equip: "+safety.getType());
    }

    public void refreshData(){
        safetyList = new ArrayList<>();
        safetyList.addAll(databaseQueryClass.getAllSafety());

        // making the recycler split in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0
        safetyListRecyclerViewAdapter = new SafetyListRecyclerViewAdapter(SafetyListActivity.this, safetyList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(safetyListRecyclerViewAdapter);
        safetyListRecyclerViewAdapter.getAvailableFilter().filter(Available);

        safetyListFilter = new SafetyListFilter(SafetyListActivity.this, safetyList);
        availableRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        availableRecyclerView.setAdapter(safetyListFilter);
        safetyListFilter.getAvailableFilter().filter(NotAvailable);
    }

}