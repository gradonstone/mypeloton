package edu.fsu.cs.mobile.mypeloton;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Messenger extends OptionsMenuExtension {

    private FirebaseListAdapter<ChatMessage> fbadapter;
    FloatingActionButton button;
    EditText sendcontent;
    String messageContent,useremail;
    Button backbutton;
    private DatabaseReference mDatabase;
    String recipient;
    ListView history;
    String user1;
    String user2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button= findViewById(R.id.send_button);
        sendcontent=findViewById(R.id.text_input);
        history = findViewById(R.id.message_history);
        backbutton = findViewById(R.id.back);
        user1 = getIntent().getExtras().getString("userID");
        user2 = getIntent().getExtras().getString("selectedID");
        Log.i("Activity: Messenger","successfully navigated to "+ FirebaseAuth.getInstance().getCurrentUser().getEmail()+"'s messenger");
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            Log.i("Messenger","getcurrentuser=null");
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                messageContent = sendcontent.getText().toString();
                useremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                recipient = "null";
                mDatabase.child("messages").child(user1).child(user2).push().setValue(new ChatMessage(messageContent,useremail,recipient));
                mDatabase.child("messages").child(user2).child(user1).push().setValue(new ChatMessage(messageContent,useremail,recipient));
                sendcontent.setText("");
                //displayMessages();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                finish();
            }
        });

        displayMessages();
    }

    private void displayMessages(){

        fbadapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.ind_message,FirebaseDatabase.getInstance().getReference().child("messages")
                .child(user1).child(user2)){
            @Override
            protected void populateView(View v, ChatMessage model, int position){
                TextView messagecontent,messageuser,messagetime;
                messagecontent = v.findViewById(R.id.text);
                messageuser = v.findViewById(R.id.user_output);
                messagetime = v.findViewById(R.id.time);

                messagecontent.setText(model.getText());
                messageuser.setText(model.getUser());
                messagetime.setText(DateFormat.format("MM-dd-yyyy HH:mm:ss",model.getTime()));
                //Log.i("Messenger",model.getText()+"maybe?");
                Log.i("Messenger",messagecontent.getText().toString());
                Log.i("Messenger",messageuser.getText().toString());
                //Log.i("Messenger", "populateView");

            }
        };
        history.setAdapter(fbadapter);
    }

}


