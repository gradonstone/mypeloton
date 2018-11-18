package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements LocationListener{

    private DatabaseReference mDatabase;
    private Spinner distanceSpinner, timeSpinner, typeSpinner;
    private Button requestButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid, email;

    LocationManager lm;
    double longitude, latitude;

    //----------Options Menu-------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            // todo: clear back log
            case R.id.log_out:
            {
                Intent mIntent = new Intent(SearchActivity.this, MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(mIntent);
                finish();
                break;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        distanceSpinner = (Spinner) findViewById(R.id.distance_spinner);
        timeSpinner = findViewById(R.id.time_spinner);
        typeSpinner = findViewById(R.id.type_spinner);
        requestButton = findViewById(R.id.request_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        final FirebaseUser user = mAuth.getCurrentUser();
        uid = (String) getIntent().getExtras().get("uid");
        email = (String) getIntent().getExtras().get("email");
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeString = timeSpinner.getSelectedItem().toString();
                String distanceString = distanceSpinner.getSelectedItem().toString();
                String type = typeSpinner.getSelectedItem().toString();
                int time = Integer.parseInt(timeString);
                int distance = Integer.parseInt(distanceString);
                String userID = uid;
                String userEmail = email;

                //------------Get current location-------------
                getLocation();
                writeNewRequest(userID, userEmail, type, distance, time, longitude, latitude);
                Intent myIntent = new Intent(SearchActivity.this, RequestActivity.class);
                myIntent.putExtra(SearchService.UID, uid);
                // start SearchService here

                // todo: start search service
                startActivity(myIntent);

                myIntent = new Intent(SearchActivity.this, SearchService.class);
                myIntent.putExtra(SearchService.UID, uid);
                myIntent.putExtra(SearchService.LONGITUDE, longitude);
                myIntent.putExtra(SearchService.LATITUDE, latitude);
                myIntent.putExtra(SearchService.DISTANCE, distance);
                startService(myIntent);
            }
        });

        Integer[] distanceSpinnerArray = new Integer[20];
        for (int i = 1; i <= 20; i++)
        {
            distanceSpinnerArray[i-1] = i;
        }

        Integer[] timeSpinnerArray = new Integer[12];
        for (int i = 1; i <= timeSpinnerArray.length; i++)
        {
            timeSpinnerArray[i-1] = i*5;
        }

        ArrayAdapter<Integer> timeAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, timeSpinnerArray
        );

        ArrayAdapter<Integer> distanceAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, distanceSpinnerArray
        );

        distanceSpinner.setAdapter(distanceAdapter);
        timeSpinner.setAdapter(timeAdapter);

    }

    private void writeNewRequest(String userID, String email, String ride_type, int distance, int time, double longitude, double latitude)
    {
        //create a request object
        Request request = new Request(userID, email, ride_type, distance, time, longitude, latitude, 1);
        //throw it in the database under the requests 'table'
        mDatabase.child("requests").child(uid).setValue(request);
    }

    @Override
    protected void onStart(){
        super.onStart();

    }

    void getLocation() {
        try {
            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        }
        catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(SearchActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }

}