package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.app.VoiceInteractor;
import android.content.Context;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends OptionsMenuExtension {

    private DatabaseReference mDatabase;
    private Spinner distanceSpinner, timeSpinner, typeSpinner;
    private Button requestButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private AuthCredential credential;
    double longitude, latitude;

    //Migrating from Request Activity
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private String mLocationLabel;
    private TextView mLocationText;

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



        mLocationLabel = getResources().getString(R.string.location_label);
        mLocationText = (TextView) findViewById((R.id.location_text));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeString = timeSpinner.getSelectedItem().toString();
                String distanceString = distanceSpinner.getSelectedItem().toString();
                String type = typeSpinner.getSelectedItem().toString();
                int time = Integer.parseInt(timeString);
                int distance = Integer.parseInt(distanceString);
                String userID = uid;
                String userEmail = user.getEmail().toString();

                //------------Get current location-------------
                getLastLocation();
                Request userRequest = writeNewRequest(userID, userEmail, type, distance, time, longitude, latitude);
                Intent myIntent = new Intent(SearchActivity.this, RequestActivity.class);
                myIntent.putExtra("uid", uid);
                myIntent.putExtra("userRequest", userRequest);
                myIntent.putExtra("distance", distanceString);
                myIntent.putExtra("rideType", type);
                // start SearchService here

                // todo: start search service

                startActivity(myIntent);
            }
        });

        Integer[] distanceSpinnerArray = new Integer[20];
        for (int i = 1; i <= 20; i++)
        {
            distanceSpinnerArray[i-1] = i;
        }

        Integer[] timeSpinnerArray = new Integer[12];
        int startTime = 20;
        for (int i = 0; i < timeSpinnerArray.length; i++)
        {
            timeSpinnerArray[i] = i*5 + startTime;
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

    private Request writeNewRequest(String userID, String email, String ride_type, int distance, int time, double longitude, double latitude)
    {
        //create a request object
        Request request = new Request(userID, email, ride_type, distance, time, longitude, latitude, 1);
        //throw it in the database under the requests 'table'
        mDatabase.child("requests").child(uid).setValue(request);
        return request;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            mLastLocation = task.getResult();
                            //TO-DO: Push latitude and longitude into database
                            latitude = mLastLocation.getLatitude();
                            longitude = mLastLocation.getLongitude();
                            String cityName = "";
                            String stateName = "";
                            Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
                            try {
                                List <Address> address = geocoder.getFromLocation(latitude, longitude, 1);

                                if (!address.isEmpty()) {
                                    cityName = address.get(0).getLocality();
                                    stateName = address.get(0).getAdminArea();
                                }
                                else {
                                    cityName = "Unknown";
                                    stateName = "Unknown";
                                }

                            } catch (IOException e) {
                                e.printStackTrace();

                            }

                            mLocationText.setText(String.format(Locale.ENGLISH, "%s: %s, %s",
                                    mLocationLabel,
                                    cityName, stateName));


                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.activity_request_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(SearchActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            startLocationPermissionRequest();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}