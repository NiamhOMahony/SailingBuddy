package com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.niamh.sailingbuddy.DashboardActivity;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.R;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.Safety;
import com.niamh.sailingbuddy.SafetyCRUD.CreateSafety.SafetyCreateListener;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class SafetyListActivity2 extends AppCompatActivity implements SafetyCreateListener {



    private final DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);
    EditText inputSearch;
    ImageView killcord;
    ImageView grabbag;
    ImageView vhf;
    ImageView firstaid;
    ImageView dinghy;
    ImageView anchor;
    ImageView bouy;
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

    SwipeRefreshLayout recyclerSwipeRefresh;
    SwipeRefreshLayout availSwipeRefresh;


    public List<Safety> safetyList;

    //Passing information to the main content activity
    private TextView safetyListEmptyTextView;
    private SafetyListRecyclerViewAdapter2 safetyListRecyclerViewAdapter2;
    private SafetyListFilter2 safetyListFilter2;

    LinearLayout layoutItems;
    TextView safetyEquipTextView;

    Animation anim_from_button, anim_from_top, anim_from_left, shakeAnimation;

    public RecyclerView recyclerView;
    public RecyclerView availableRecyclerView;

    //When class is created the folowing attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_list2);

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

        refreshData();

        viewVisibility();

        FloatingActionButton fab = findViewById(R.id.fab);
        ImageView deleteImageView = findViewById(R.id.deleteImageView);
        fab.setVisibility(View.GONE);
        deleteImageView.setVisibility(View.GONE);
        LinearLayout layoutSearch = findViewById(R.id.layoutSearch);
        ImageView searchImage = findViewById(R.id.searchImage);

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
                availSwipeRefresh.setRefreshing(false)                                          ;
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
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(KillCord);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(KillCord);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When GrabBag image is clicked all items in list that are type GrabBag show up
        grabbag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(GrabBag);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(GrabBag);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When VHF image is clicked all items in list that are type VHF show up
        vhf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(VHF);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(VHF);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When FirstAid image is clicked all items in list that are type FirstAid show up
        firstaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(FirstAid);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(FirstAid);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }

        });
        //When dinghy image is clicked all items in list that are type dinghy show up
        dinghy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(SafetyBoat);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(SafetyBoat);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When anchor image is clicked all items in list that are type anchor show up
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(Anchor);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(Anchor);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When bouy image is clicked all items in list that are type bouy show up
        bouy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(All);
                safetyListRecyclerViewAdapter2.getFilter2().filter(Bouy);

                safetyListFilter2.getFilter2().filter(All);
                safetyListFilter2.getFilter2().filter(Bouy);

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                safetyListRecyclerViewAdapter2.getFilter2().filter(s);
                safetyListFilter2.getFilter2().filter(s);
                search = s;

                safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(this, R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(this, R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(this, R.anim.anim_from_left);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        //Set Annimations
        safetyEquipTextView.setAnimation(anim_from_top);

        layoutSearch.setAnimation(anim_from_top);
        inputSearch.setAnimation(anim_from_top);
        searchImage.setAnimation(anim_from_top);
        layoutItems.setAnimation(anim_from_left);
        recyclerView.setAnimation(anim_from_left);
        availableRecyclerView.setAnimation(anim_from_left);
        bottomNavigationView.setAnimation(anim_from_button);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void viewVisibility() {
        if(safetyList.isEmpty())
            safetyListEmptyTextView.setVisibility(View.VISIBLE);
        else
            safetyListEmptyTextView.setVisibility(View.GONE);
    }

    //when new data is added update main recycler view
    @Override
    public void onSafetyCreated(Safety safety) {
        safetyList.add(safety);
        safetyListRecyclerViewAdapter2.notifyDataSetChanged();
        safetyListFilter2.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_FYP***","Create Safety Equip: "+safety.getType());
    }

    public void refreshData(){
        safetyList = new ArrayList<>();
        safetyList.addAll(databaseQueryClass.getAllSafety());

        // making the recycler split in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0
        safetyListRecyclerViewAdapter2 = new SafetyListRecyclerViewAdapter2(SafetyListActivity2.this, safetyList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(safetyListRecyclerViewAdapter2);
        safetyListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);

        safetyListFilter2 = new SafetyListFilter2(SafetyListActivity2.this, safetyList);
        availableRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        availableRecyclerView.setAdapter(safetyListFilter2);
        safetyListFilter2.getAvailableFilter2().filter(NotAvailable);
    }

}