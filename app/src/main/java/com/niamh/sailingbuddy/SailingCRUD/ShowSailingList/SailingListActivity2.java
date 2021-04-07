package com.niamh.sailingbuddy.SailingCRUD.ShowSailingList;

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
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.SailingCreateListener;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class SailingListActivity2 extends AppCompatActivity implements SailingCreateListener {



    private final DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);
    EditText inputSearch;
    TextView topaz;
    TextView lazer;
    TextView vago;
    TextView feva;
    TextView mirror;
    TextView pico;
    TextView optimist;
    TextView all;
    CharSequence search = "";

    CharSequence Topaz = "topaz";
    CharSequence Lazer = "lazer";
    CharSequence Vago = "vago";
    CharSequence Feva = "feva";
    CharSequence Mirror = "mirror";
    CharSequence Pico = "pico";
    CharSequence Optimist = "optimist";

    CharSequence Available = "Yes";
    CharSequence NotAvailable = "No";

    CharSequence All = "";

    public List<Sailing> sailingList;

    SwipeRefreshLayout recyclerSwipeRefresh;
    SwipeRefreshLayout availSwipeRefresh;

    //Passing information to the main content activity
    private TextView sailingListEmptyTextView;
    private SailingListRecyclerViewAdapter2 sailingListRecyclerViewAdapter2;
    private SailingListFilter2 sailingListFilter2;

    LinearLayout layoutItems;
    TextView sailingEquipTextView;

    Animation anim_from_button, anim_from_top, anim_from_left, shakeAnimation;

    public RecyclerView recyclerView;
    public RecyclerView availableRecyclerView;

    //When class is created the folowing attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sailing_list2);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        recyclerView = findViewById(R.id.recyclerView);
        availableRecyclerView = findViewById(R.id.availableRecyclerView);

        sailingListEmptyTextView = findViewById(R.id.emptyListTextView);
        layoutItems = findViewById(R.id.layoutItems);
        sailingEquipTextView = findViewById(R.id.sailingEquipTextView);

        topaz = findViewById(R.id.topaz);
        lazer = findViewById(R.id.lazer);
        vago = findViewById(R.id.vago);
        feva = findViewById(R.id.feva);
        mirror = findViewById(R.id.mirror);
        pico = findViewById(R.id.pico);
        optimist = findViewById(R.id.oppi);
        all = findViewById(R.id.all);

        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.setHint("Search Sailing Equipment Descriptions");

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
        //Have to go back to all after each click must fix
        //When all is clicked all items in list show up
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(All);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When Topaz image is clicked all items in list that are type killcord show up
        topaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Topaz);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Topaz);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When lazer image is clicked all items in list that are type GrabBag show up
        lazer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Lazer);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Lazer);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When vago image is clicked all items in list that are type VHF show up
        vago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Vago);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Vago);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When FirstAid image is clicked all items in list that are type FirstAid show up
        feva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Feva);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Feva);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When mirror image is clicked all items in list that are type dinghy show up
        mirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Mirror);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Mirror);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When pico image is clicked all items in list that are type anchor show up
        pico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Pico);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Pico);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);

            }
        });
        //When optomist image is clicked all items in list that are type bouy show up
        optimist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(All);
                sailingListRecyclerViewAdapter2.getFilter2().filter(Optimist);

                sailingListFilter2.getFilter2().filter(All);
                sailingListFilter2.getFilter2().filter(Optimist);

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
            }
        });
        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sailingListRecyclerViewAdapter2.getFilter2().filter(s);
                sailingListFilter2.getFilter2().filter(s);
                search = s;

                sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);
                sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
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
        sailingEquipTextView.setAnimation(anim_from_top);

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
        if(sailingList.isEmpty())
            sailingListEmptyTextView.setVisibility(View.VISIBLE);
        else
            sailingListEmptyTextView.setVisibility(View.GONE);
    }

    //when new data is added update main recycler view
    @Override
    public void onSailingCreated(Sailing sailing) {
        sailingList.add(sailing);
        sailingListRecyclerViewAdapter2.notifyDataSetChanged();
        sailingListFilter2.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_FYP***","Create Sailing Equip: "+sailing.getType());
    }

    public void refreshData(){
        sailingList = new ArrayList<>();
        sailingList.addAll(databaseQueryClass.getAllSailing());

        // making the recycler split in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0
        sailingListRecyclerViewAdapter2 = new SailingListRecyclerViewAdapter2(SailingListActivity2.this, sailingList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(sailingListRecyclerViewAdapter2);
        sailingListRecyclerViewAdapter2.getAvailableFilter2().filter(Available);

        sailingListFilter2 = new SailingListFilter2(SailingListActivity2.this, sailingList);
        availableRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        availableRecyclerView.setAdapter(sailingListFilter2);
        sailingListFilter2.getAvailableFilter2().filter(NotAvailable);
    }

}

