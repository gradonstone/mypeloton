package edu.fsu.cs.mobile.mypeloton;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button register,login;
    EditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.register_button);
        login = findViewById(R.id.login_button);
        username = findViewById(R.id.username_edit);
        password = findViewById(R.id.pass_edit);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("Main Activity","Pressed Login Button");
                boolean is_empty = true;
                if(username.getText().toString() == "")
                    is_empty = false;
                else if(password.getText().toString() == "")
                    is_empty = false;

                if(!is_empty)
                    Toast.makeText(MainActivity.this, "Please Enter both UserName and Password", Toast.LENGTH_SHORT).show();
                //elseif credentials dont exist then toast that credentials are invalid
                //else intent move activities

                //--------- for testing purposes ------------
                // need to validate login obviously
                Intent myIntent  = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(myIntent);

            }

        });
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("Main Activity","Pressed Register Button");
                //move to the register activity
            }
        });

    }
}
