package edu.fsu.cs.mobile.mypeloton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // first, get all data from database
        // create service to check database every 30 seconds or so
        // list adapter to show all matches, otherwise, show the text field searching...

    }
}
