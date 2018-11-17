package edu.fsu.cs.mobile.mypeloton;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class SearchService extends IntentService {
    private static final String TAG = "SearchService";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String DISTANCE = "distance";
    public static final String UID = "uid";

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


        }
    }
}
