package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

//Code inspired by lecture9examples>locationexaples

public class RequestActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_LOCATIONS = 3;
    private TextView mTextStatus;
    private LocationManager mLocationManager;
    private boolean mLocationPermissionGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestLocationUpdates();

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
