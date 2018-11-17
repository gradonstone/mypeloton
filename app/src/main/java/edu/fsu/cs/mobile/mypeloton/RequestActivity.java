package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//Code inspired by lecture9examples>locationexaples

public class RequestActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_LOCATIONS = 3;
    private TextView mTextStatus;
    private LocationManager mLocationManager;
    private boolean mLocationPermissionGranted = false;
    private DatabaseReference mDatabase;
    private Button cancelRequest;
    private String uid;
    private List<Request> requestList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        uid = getIntent().getExtras().getString("uid");
        cancelRequest = (Button) findViewById(R.id.cancel_request_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        int time = 5;

        requestList = new ArrayList<>();
        mDatabase.child("requests").orderByChild("time").equalTo(time)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        requestList.clear();
                        if(dataSnapshot.exists()){
                            for(DataSnapshot requestSnapshot : dataSnapshot.getChildren()){
                                String userID = requestSnapshot.child("userID").getValue(String.class);
                                String email = requestSnapshot.child("email").getValue(String.class);
                                String rideType = requestSnapshot.child("ride_type").getValue(String.class);
                                int time = requestSnapshot.child("time").getValue(Integer.class);
                                int distance = requestSnapshot.child("distance").getValue(Integer.class);
                                int active = requestSnapshot.child("active").getValue(Integer.class);
                                int longitude = requestSnapshot.child("longitude").getValue(Integer.class);
                                int latitude = requestSnapshot.child("latitude").getValue(Integer.class);
                                Request request = new Request(userID, email, rideType, distance, time, longitude, latitude, active);
                                requestList.add(request);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RequestActivity.this, "Database read failed", Toast.LENGTH_LONG).show();
                    }
                });

        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("requests").child(uid).child("active").setValue(0);
                Intent myIntent = new Intent(RequestActivity.this, SearchActivity.class);
                myIntent.putExtra("uid", uid);
                startActivity(myIntent);
            }
        });

        //mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //requestLocationUpdates();

        // first, get all data from database
        // create service to check database every 30 seconds or so
        // list adapter to show all matches, otherwise, show the text field searching...

    }

    @Override
    protected void onResume() {
        super.onResume();
        //showLastKnownLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mLocationManager.removeUpdates(mLocationListener);
    }

    public void requestLocationUpdates(){
        Toast.makeText(getApplicationContext(), "Waiting for location ...",
                Toast.LENGTH_SHORT).show();
        if(!mLocationPermissionGranted) {
            requestLocationsPermission();
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
        } catch (SecurityException e) {
            requestLocationsPermission();
        }
    }

    private void requestLocationsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(RequestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(RequestActivity.this);
            builder.setTitle("Requesting internet permissions");
            builder.setMessage("This application requires internet. Accept to continue");
            builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(RequestActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSION_REQUEST_LOCATIONS);
                }
            });
            builder.show();
        }
        else {
            ActivityCompat.requestPermissions(RequestActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_LOCATIONS);
        }

    }


    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Waiting for location",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Connection Lost",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLocationChanged(Location location) {
            Geocoder geo = new Geocoder(getApplicationContext());
            List<Address> addresses = null;

            try {
                addresses = geo.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 10);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses == null) {
                return;
            }
            if (addresses.size() > 0 &&
                    addresses.get(0).getMaxAddressLineIndex() >= 0)
                mTextStatus.setText(addresses.get(0).getAddressLine(0));
        }
    };

}
