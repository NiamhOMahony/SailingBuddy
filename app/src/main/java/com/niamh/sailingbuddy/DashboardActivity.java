package com.niamh.sailingbuddy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.niamh.sailingbuddy.Database.DatabaseQueryClass;
import com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList.SafetyListActivity;
import com.niamh.sailingbuddy.SafetyCRUD.ShowSafetyList.SafetyListActivity2;
import com.niamh.sailingbuddy.SailingCRUD.ShowSailingList.SailingListActivity;
import com.niamh.sailingbuddy.SailingCRUD.ShowSailingList.SailingListActivity2;
import com.niamh.sailingbuddy.SessionCRUD.ShowSessionList.SessionListActivity;
import com.niamh.sailingbuddy.UserCRUD.CreateUser.User;
import com.niamh.sailingbuddy.UserCRUD.ProfileActivity;
import com.niamh.sailingbuddy.UserCRUD.ShowUserList.UserListActivity;
import com.niamh.sailingbuddy.UserCRUD.ShowUserList.UserListActivity2;
import com.niamh.sailingbuddy.Weather.FindCity;
import com.niamh.sailingbuddy.Weather.LocationServicesFind;
import com.niamh.sailingbuddy.Weather.WeatherData;

import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.niamh.sailingbuddy.UserCRUD.LoginActivity.mypreference;

public class DashboardActivity extends AppCompatActivity {

    User user;

    String typeK = "TYPE_KEY";
    String IdK = "ID_KEY";

    Animation anim_from_button;
    Animation anim_from_top;
    Animation anim_from_left;
    Animation anim_to_left;
    Animation anim_to_right;
    Animation anim_from_right;
    Animation shakeAnimation;

    //Live weather app inspiration-  from https://www.youtube.com/watch?v=FtmI0qqQsl8
    final String APP_ID = "e3e3b1951491a7320f7f3e7dac02b2f6";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    String Location_Provider = LocationManager.GPS_PROVIDER;

    TextView NameofCity, weatherState, Temperature;
    ImageView mweatherIcon;

    LocationManager mLocationManager;
    LocationListener mLocationListner;

    RelativeLayout mCityFinder;

    ConstraintLayout dashboardHeader;
    TextView name;
    CircleImageView profileImageView;

    LinearLayout safetyLayout;
    LinearLayout sailingLayout;
    LinearLayout groupLayout;
    ConstraintLayout weatherConstraintLayout;
    LinearLayout sessionLayout;

    ImageView safetyImageView;
    ImageView sailingImageView;
    ImageView sessionImageView;
    ImageView groupImageView;

    SharedPreferences sharedpreferences;
    DatabaseQueryClass databaseQueryClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        databaseQueryClass = new DatabaseQueryClass(this);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        int id = sharedpreferences.getInt(IdK, 0);

        user = databaseQueryClass.getUserById(id);

        requestPermission();

        //Header Variables
        dashboardHeader = findViewById(R.id.dashboardHeader);
        profileImageView = findViewById(R.id.profileImageView);
        name = findViewById(R.id.nameTextView);

        String helloMessage = "Hello " + user.getName();
        name.setText(helloMessage);
        profileImageView.setImageBitmap(user.getImage());

        String type = sharedpreferences.getString(typeK, "");

        weatherState = findViewById(R.id.weatherCondition);
        Temperature = findViewById(R.id.temperature);
        mweatherIcon = findViewById(R.id.WeatherIcon);
        NameofCity = findViewById(R.id.cityName);
        mCityFinder = findViewById(R.id.cityFinder);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.menuHome);

         safetyImageView= findViewById(R.id.safetyImageView);
         sailingImageView = findViewById(R.id.userImageView);
         sessionImageView = findViewById(R.id.sessionImageView);
         groupImageView = findViewById(R.id.groupImageView);

        safetyLayout= findViewById(R.id.safetyLayout);
        sailingLayout= findViewById(R.id.sailingLayout);
        sessionLayout= findViewById(R.id.sessionLayout);
        groupLayout= findViewById(R.id.groupLayout);
        weatherConstraintLayout= findViewById(R.id.weatherConstraintLayout);

        //Load Animations
        anim_from_button = AnimationUtils.loadAnimation(this, R.anim.anim_from_bottom);
        anim_from_top = AnimationUtils.loadAnimation(this, R.anim.anim_from_top);
        anim_from_left = AnimationUtils.loadAnimation(this, R.anim.anim_from_left);
        anim_from_right = AnimationUtils.loadAnimation(this, R.anim.anim_from_right);
        anim_to_left = AnimationUtils.loadAnimation(this, R.anim.left_out);
        anim_to_right = AnimationUtils.loadAnimation(this, R.anim.right_out);
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        //Set Annimations
        dashboardHeader.setAnimation(anim_from_top);

        sailingLayout.setAnimation(anim_from_left);
        sessionLayout.setAnimation(anim_from_left);
        safetyLayout.setAnimation(anim_from_left);
        groupLayout.setAnimation(anim_from_left);
        weatherConstraintLayout.setAnimation(anim_from_left);
        bottomNavigationView.setAnimation(anim_from_button);


        mCityFinder.setOnClickListener(v -> {
            Intent CityIntent = new Intent(DashboardActivity.this, FindCity.class);
            startActivity(CityIntent);
        });


        /*Code adapted from How to Implement Bottom Navigation With Activities in Android Studio
         | BottomNav | Android Coding https://www.youtube.com/watch?v=JjfSjMs0ImQ*/

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.menuSettings:
                    Intent SettingsIntent = new Intent(DashboardActivity.this, SettingsActivity.class);
                    startActivity(SettingsIntent);
                    overridePendingTransition(0,0);
                    return true;
                case R.id.menuHome:
                    return true;
                case R.id.menuUser:
                    Intent profileIntent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });



        groupImageView.setOnClickListener(v -> {
            Intent groupIntent;
            if(type.equals("Admin")) {
                groupIntent = new Intent(getApplicationContext(), UserListActivity.class);
            }else {
                groupIntent = new Intent(getApplicationContext(), UserListActivity2.class);
            }
            startActivity(groupIntent);
        });

        sessionLayout.setOnClickListener(v -> {
            Intent sessionItent = new Intent(getApplicationContext(), SessionListActivity.class);
            startActivity(sessionItent);
        });

        safetyImageView.setOnClickListener(v -> {
            Intent safetyIntent;
            if(type.equals("Admin")) {
                safetyIntent = new Intent(getApplicationContext(), SafetyListActivity.class);
            }else {
                safetyIntent = new Intent(getApplicationContext(), SafetyListActivity2.class);
            }
            startActivity(safetyIntent);
        });

        sailingImageView.setOnClickListener(v -> {
            Intent sailingIntent;
            if(type.equals("Admin")) {
                sailingIntent = new Intent(getApplicationContext(), SailingListActivity.class);
            }else {
                sailingIntent = new Intent(getApplicationContext(), SailingListActivity2.class);
            }
            startActivity(sailingIntent);
        });

        // Asking for GPS permission if it's not granted by user
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            // If user has given permission then starting location service to get geographic coordinates.
            String text = "Getting location";
            NameofCity.setText(text);
            Intent intent = new Intent(DashboardActivity.this, LocationServicesFind.class);
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent=getIntent();
        String city= mIntent.getStringExtra("City");
        if(city!=null)
        {
            getWeatherForNewCity(city);
        }
        else
        {
            getWeatherForCurrentLocation();
        }


    }


    private void getWeatherForNewCity(String city)
    {
        RequestParams params=new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsdoSomeNetworking(params);

    }




    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                String Latitude = String.valueOf(location.getLatitude());
                String Longitude = String.valueOf(location.getLongitude());

                RequestParams params =new RequestParams();
                params.put("lat" ,Latitude);
                params.put("lon",Longitude);
                params.put("appid",APP_ID);
                letsdoSomeNetworking(params);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                //not able to get location
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(DashboardActivity.this,"Location Services Granted",Toast.LENGTH_SHORT).show();
                getWeatherForCurrentLocation();
            }
            else
            {
                Toast.makeText(DashboardActivity.this,"Location Services Denied",Toast.LENGTH_SHORT).show();
            }
        }


    }



    private  void letsdoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(DashboardActivity.this,"Data Get Success",Toast.LENGTH_SHORT).show();

                WeatherData weatherD= WeatherData.fromJson(response);
                updateUI(weatherD);


                // super.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });



    }

    private  void updateUI(WeatherData weather){


        Temperature.setText(weather.getmTemperature());
        NameofCity.setText(weather.getMcity());
        weatherState.setText(weather.getmWeatherType());
        int resourceID=getResources().getIdentifier(weather.getMicon(),"drawable",getPackageName());



    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(DashboardActivity.this, "All permissions are granted!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(DashboardActivity.this, "All permissions are Denied!", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).
                withErrorListener(error -> Toast.makeText(DashboardActivity.this, "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

}