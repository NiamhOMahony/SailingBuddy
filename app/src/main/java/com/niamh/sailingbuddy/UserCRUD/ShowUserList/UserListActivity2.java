package com.niamh.sailingbuddy.UserCRUD.ShowUserList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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
import com.niamh.sailingbuddy.SettingsActivity;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.UserCreateDialogFragment;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.UserCreateListener;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;
import com.niamh.sailingbuddy.Utils.Config;

import java.util.ArrayList;
import java.util.List;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class UserListActivity2 extends AppCompatActivity implements UserCreateListener {

    private final DatabaseQueryClass databaseQueryClass = new DatabaseQueryClass(this);

    EditText inputSearch;
    CharSequence search = "";

    CharSequence Admin = "Admin";
    CharSequence Instructor = "Instructor";
    String typeK = "TYPE_KEY";
    SharedPreferences sharedpreferences;

    private final List<User> userList = new ArrayList<>();

    //Passing information to the main content activity
    private TextView sailingListEmptyTextView;
    private UserListRecyclerViewAdapter2 userListRecyclerViewAdapter2;


    Animation anim_from_button, anim_from_top, anim_from_left, shakeAnimation;


    TextView userTextView;

    //When class is created the folowing attributes get their values passed to them
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list2);

        //Stop Keyboard automatically popping up because of edit text
        //used from https://stackoverflow.com/questions/2496901/android-on-screen-keyboard-auto-popping-up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        RecyclerView adminRecyclerView = findViewById(R.id.adminRecyclerView);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        String type = sharedpreferences.getString(typeK, "");

        sailingListEmptyTextView = findViewById(R.id.emptyListTextView);

        userTextView = findViewById(R.id.userTextView);


        inputSearch = findViewById(R.id.inputSearch);
        inputSearch.setHint("Search Users");

        userList.addAll(databaseQueryClass.getAllUsers());

        // making the recycler split in two rows
        //adapted from https://www.youtube.com/watch?v=BrLnsDkoba0
        userListRecyclerViewAdapter2 = new UserListRecyclerViewAdapter2(this, userList);
        adminRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
        adminRecyclerView.setAdapter(userListRecyclerViewAdapter2);


        viewVisibility();


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

        //When text is typed into the search bar it has an automatic response by changing the recycler View
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userListRecyclerViewAdapter2.getNameFilter().filter(s);
                search = s;
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
        userTextView.setAnimation(anim_from_top);
        layoutSearch.setAnimation(anim_from_top);
        searchImage.setAnimation(anim_from_top);
        inputSearch.setAnimation(anim_from_top);
        adminRecyclerView.setAnimation(anim_from_left);
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
        alertDialogBuilder.setMessage("Are you sure, You wanted to delete all Users?");
        //If you select yes after the warning the table clears
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    boolean isAllDeleted = databaseQueryClass.deleteAllUser();
                    if(isAllDeleted){
                        userList.clear();
                        userListRecyclerViewAdapter2.notifyDataSetChanged();
                        viewVisibility();
                    }
                });

        //If you select no you're brought back to the "Homepage"
        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void viewVisibility() {
        if(userList.isEmpty())
            sailingListEmptyTextView.setVisibility(View.VISIBLE);
        else
            sailingListEmptyTextView.setVisibility(View.GONE);
    }

    private void openUserCreateDialog() {
        UserCreateDialogFragment userCreateDialogFragment = UserCreateDialogFragment.newInstance(this);
        userCreateDialogFragment.show(getSupportFragmentManager(), Config.CREATE_USER);
    }

    //when new data is added update main recycler view
    @Override
    public void onUserCreated (User user) {
        userList.add(user);
        userListRecyclerViewAdapter2.notifyDataSetChanged();
        viewVisibility();
        Log.d("***NIAMH_FYP***","Create User: "+user.getType());
    }
}