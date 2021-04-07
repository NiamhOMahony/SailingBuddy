package com.niamh.sailingbuddy.SailingCRUD.ShowSailingList;

//Adapted from Michael Gleesons lecture on 12/11/2020 gleeson.io

import android.content.Intent;
import android.graphics.Path;
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
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.Sailing;
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.SailingCreateDialogFragment;
import com.niamh.sailingbuddy.SailingCRUD.CreateSailing.SailingCreateListener;
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.List;

public class SailingListActivity extends AppCompatActivity implements SailingCreateListener {



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

    //Passing information to the main content activity
    private TextView sailingListEmptyTextView;
    private SailingListRecyclerViewAdapter sailingListRecyclerViewAdapter;
    private SailingListFilter sailingListFilter;

    Animation anim_from_button, anim_from_top, anim_from_left, shakeAnimation;

    LinearLayout layoutItems;
    TextView sailingEquipTextView;

    SwipeRefreshLayout recyclerSwipeRefresh;
    SwipeRefreshLayout availSwipeRefresh;

    public RecyclerView recyclerView;
    public RecyclerView availableRecyclerView;

    //When class is created the folowing attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sailing_list);

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

        // when bin view selected were are given the option to delete all
        ImageView deleteImageView = findViewById(R.id.deleteImageView);
        deleteImageView.setOnClickListener(v -> onDeleteAllSelected());

        //float action button brings us to the create fragment
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> openSailingCreateDialog());

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
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(All);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When Topaz image is clicked all items in list that are type killcord show up
        topaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Topaz);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Topaz);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When lazer image is clicked all items in list that are type GrabBag show up
        lazer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Lazer);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Lazer);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When vago image is clicked all items in list that are type VHF show up
        vago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Vago);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Vago);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When FirstAid image is clicked all items in list that are type FirstAid show up
        feva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Feva);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Feva);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When mirror image is clicked all items in list that are type dinghy show up
        mirror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Mirror);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Mirror);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When pico image is clicked all items in list that are type anchor show up
        pico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Pico);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Pico);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);

            }
        });
        //When optomist image is clicked all items in list that are type bouy show up
        optimist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sailingListRecyclerViewAdapter.getFilter().filter(All);
                sailingListRecyclerViewAdapter.getFilter().filter(Optimist);

                sailingListFilter.getFilter().filter(All);
                sailingListFilter.getFilter().filter(Optimist);

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }
        });
        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sailingListRecyclerViewAdapter.getFilter().filter(s);
                sailingListFilter.getFilter().filter(s);
                search = s;

                sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);
                sailingListFilter.getAvailableFilter().filter(NotAvailable);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        LinearLayout layoutSearch = findViewById(R.id.layoutSearch);
        ImageView searchImage = findViewById(R.id.searchImage);

        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(this, R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(this, R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(this, R.anim.anim_from_left);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        //Set Annimations
        sailingEquipTextView.setAnimation(anim_from_top);
        deleteImageView.setAnimation(anim_from_top);
        layoutSearch.setAnimation(anim_from_top);
        searchImage.setAnimation(anim_from_top);
        inputSearch.setAnimation(anim_from_top);
        layoutItems.setAnimation(anim_from_left);
        recyclerView.setAnimation(anim_from_left);
        availableRecyclerView.setAnimation(anim_from_left);
        fab.setAnimation(anim_from_button);
        bottomNavigationView.setAnimation(anim_from_button);



    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //delete all fragment
    private void onDeleteAllSelected() {
        //When delete menu action is clicked the program asks if youre sure
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete all Sailing Equipment?");
        //If you select yes after the warning the table clears
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    boolean isAllDeleted = databaseQueryClass.deleteAllSailing();
                    if(isAllDeleted){
                        sailingList.clear();
                        sailingListRecyclerViewAdapter.notifyDataSetChanged();
                        sailingListFilter.notifyDataSetChanged();
                        viewVisibility();
                    }
                });

        //If you select no you're brought back to the "Homepage"
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void viewVisibility() {
        if(sailingList.isEmpty())
            sailingListEmptyTextView.setVisibility(View.VISIBLE);
        else
            sailingListEmptyTextView.setVisibility(View.GONE);
    }

    private void openSailingCreateDialog() {
        SailingCreateDialogFragment sailingCreateDialogFragment = SailingCreateDialogFragment.newInstance(this);
        sailingCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_SAILING);
    }

    //when new data is added update main recycler view
    @Override
    public void onSailingCreated(Sailing sailing) {
        sailingList.add(sailing);
        sailingListRecyclerViewAdapter.notifyDataSetChanged();
        sailingListFilter.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_FYP***","Create Sailing Equip: "+sailing.getType());
    }

    public void refreshData(){
        sailingList = new ArrayList<>();
        sailingList.addAll(databaseQueryClass.getAllSailing());

        // making the recycler split in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0
        sailingListRecyclerViewAdapter = new SailingListRecyclerViewAdapter(this, sailingList);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        recyclerView.setAdapter(sailingListRecyclerViewAdapter);
        sailingListRecyclerViewAdapter.getAvailableFilter().filter(Available);

        sailingListFilter = new SailingListFilter(this, sailingList);
        availableRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));
        availableRecyclerView.setAdapter(sailingListFilter);
        sailingListFilter.getAvailableFilter().filter(NotAvailable);
    }
}