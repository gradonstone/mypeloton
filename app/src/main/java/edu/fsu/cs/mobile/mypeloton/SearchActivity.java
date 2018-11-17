package edu.fsu.cs.mobile.mypeloton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Spinner distanceSpinner, timeSpinner, typeSpinner;
    private Button requestButton;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uid, email;


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
                //placeholders for now
                double longitude = 0;
                double latitude = 0;

                writeNewRequest(userID, userEmail, type, distance, time, longitude, latitude);

                Intent myIntent = new Intent(SearchActivity.this, RequestActivity.class);
                myIntent.putExtra(SearchService.UID, uid);

                // start SearchService here
                myIntent.putExtra(SearchService.LONGITUDE, longitude);
                myIntent.putExtra(SearchService.LATITUDE, latitude);
                myIntent.putExtra(SearchService.DISTANCE, distance);



                startActivity(myIntent);
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
}