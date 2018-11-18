package edu.fsu.cs.mobile.mypeloton;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class SearchService extends IntentService {
    private static final String TAG = "SearchService";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String DISTANCE = "distance";
    public static final String UID = "uid";
    double longitude, latitude;

    public SearchService() {
        super("SearchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        // may not actually be strings?
        String xLocation, yLocation, distance;
        if (extras != null) {
            // get x, y and distance
            longitude = extras.getDouble(LONGITUDE);
            latitude = extras.getDouble(LATITUDE);
            Log.i(TAG,"Longitude is =" + longitude);
            Log.i(TAG,"Latitude is = " + latitude);

        }
    }
}
