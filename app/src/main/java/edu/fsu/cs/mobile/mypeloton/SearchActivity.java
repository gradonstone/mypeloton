package edu.fsu.cs.mobile.mypeloton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private DatabaseReference timeRef, userRef, ridetypeRef, distanceRef;
    private Spinner distanceSpinner, timeSpinner, typeSpinner;
    private Button find;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        distanceSpinner = (Spinner) findViewById(R.id.distance_spinner);
        timeSpinner = findViewById(R.id.time_spinner);
        typeSpinner = findViewById(R.id.type_spinner);
        find = findViewById(R.id.button4);

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeRef.setValue(timeSpinner.getSelectedItem().toString());
                //ridetypeRef.setValue(typeSpinner.getSelectedItem().toString());
                distanceRef.setValue(distanceSpinner.getSelectedItem().toString());
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("requests").child("user");
        //timeRef = mDatabase.child("time");
        //userRef = mDatabase.child("user");
        //ridetypeRef = mDatabase.child("ride_type");
        //distanceRef = mDatabase.child("distance");

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

    @Override
    protected void onStart(){
        super.onStart();

    }
}