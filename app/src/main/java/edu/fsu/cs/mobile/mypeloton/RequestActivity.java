package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Code inspired by GoogleSamples -> Android Play Location

public class RequestActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private DatabaseReference mDatabase;
    private Button cancelRequest;
    private String uid;
    private List<Request> requestList;
    private ArrayAdapter<String> emails;
    private DisplayRequestAdapter requestAdapter;
    private int checker = 0;
    private Request request;
    private ListView displayRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        uid = getIntent().getExtras().getString("uid");
        cancelRequest = (Button) findViewById(R.id.cancel_request_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        request = (Request) getIntent().getSerializableExtra("userRequest");

        displayRequests = (ListView) findViewById(R.id.request_list);
        requestAdapter = new DisplayRequestAdapter(this, R.layout.request_item);

        requestList = new ArrayList<>();

        mDatabase.child("requests").orderByChild("ride_type").equalTo(request.getRide_type())
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
                                if(userID.matches(request.getUserID()))
                                    checker = 1;
                                if(!rideType.matches(request.getRide_type()))
                                    checker = 1;
                                if(distance != request.getDistance())
                                    checker = 1;
                                if(active == 0)
                                    checker = 1;
                                //check for long and latitude closeness
                                if(checker == 0) {
                                    Request request = new Request(userID, email, rideType, distance, time, longitude, latitude, active);
                                    requestList.add(request);
                                    requestAdapter.add(new DisplayRequest(email, userID));
                                }
                                else
                                    checker = 0;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(RequestActivity.this, "Database read failed", Toast.LENGTH_LONG).show();
                    }
                });

        displayRequests.setAdapter(requestAdapter);

        cancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("requests").child(uid).child("active").setValue(0);
                Intent myIntent = new Intent(RequestActivity.this, SearchActivity.class);
                myIntent.putExtra("uid", uid);
                startActivity(myIntent);
            }
        });
    }


}


