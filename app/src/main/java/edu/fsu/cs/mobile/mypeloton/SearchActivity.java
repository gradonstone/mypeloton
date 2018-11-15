package edu.fsu.cs.mobile.mypeloton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Spinner distanceSpinner = (Spinner) findViewById(R.id.distance_spinner);
        Spinner timeSpinner = findViewById(R.id.time_spinner);
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
}