package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity implements LocationListener{

    private DatabaseReference mDatabase;
    private Spinner distanceSpinner, timeSpinner, typeSpinner;
    private Button requestButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid;
    private AuthCredential credential;

    LocationManager lm;
    double longitude, latitude;

    //Migrating from Request Activity
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private String mLatitudeLabel;
    private String mLongitudeLabel;
    private TextView mLatitudeText;
    private TextView mLongitudeText;


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
            case R.id.delete_account:
            {
                final Intent mIntent = new Intent(SearchActivity.this, MainActivity.class);
                if(user != null)
                {
                    String idToken = user.getIdToken(true).toString();
                    if(idToken == null)
                        credential = EmailAuthProvider.getCredential(user.getEmail(), null);
                    else
                        credential = GoogleAuthProvider.getCredential(idToken, null);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                mDatabase.child("requests").orderByChild("userID").equalTo(uid)
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    snapshot.getRef().removeValue();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                startActivity(mIntent);
                                            }
                                            else
                                                task.getException();
                                        }
                                    });
                                }
                            });
                }
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

        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));
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
                getLocation();
                Request userRequest = writeNewRequest(userID, userEmail, type, distance, time, longitude, latitude);
                Intent myIntent = new Intent(SearchActivity.this, RequestActivity.class);
                myIntent.putExtra(SearchService.UID, uid);
                myIntent.putExtra("userRequest", userRequest);
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

    private Request writeNewRequest(String userID, String email, String ride_type, int distance, int time, double longitude, double latitude)
    {
        //create a request object
        Request request = new Request(userID, email, ride_type, distance, time, longitude, latitude, 1);
        //throw it in the database under the requests 'table'
        mDatabase.child("requests").child(uid).setValue(request);
        return request;
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
                            mLatitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    mLatitudeLabel,
                                    mLastLocation.getLatitude()));
                            mLongitudeText.setText(String.format(Locale.ENGLISH, "%s: %f",
                                    mLongitudeLabel,
                                    mLastLocation.getLongitude()));
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