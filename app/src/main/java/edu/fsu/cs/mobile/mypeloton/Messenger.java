package edu.fsu.cs.mobile.mypeloton;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Messenger extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> fbadapter;
    FloatingActionButton button;
    EditText sendcontent;
    String messageContent,useremail;
    private DatabaseReference mDatabase;
    String recipient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button= findViewById(R.id.send_button);
        sendcontent=findViewById(R.id.text_input);
        Log.i("Activity: Messenger","successfully navigated to "+ FirebaseAuth.getInstance().getCurrentUser().getEmail()+"'s messenger");
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                messageContent = sendcontent.getText().toString();
                useremail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(messageContent,useremail,recipient));
                sendcontent.setText("");
            }
        });

        displayMessages();
    }

    private void displayMessages(){
        /*for now will simply display all messages on the messages table.
        TO DO:
        pass in the recipients email into this activity and add to query below (display messages)
        add a user column, recipient column, message column, timestamp column.
        logic to find messages between user and recipient:
            display messages where user = FirebaseAuth.getInstance().getCurrentUser().getEmail()
                            OR recipient = other person / requested user
                            Order by timestamp
*/
        ListView history = findViewById(R.id.message_history);
        fbadapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.ind_message,FirebaseDatabase.getInstance().getReference()){
            @Override
            protected void populateView(View v, ChatMessage model, int position){
                TextView messagecontent,messageuser,messagetime;
                messagecontent = v.findViewById(R.id.text);
                messageuser = v.findViewById(R.id.user);
                messagetime = v.findViewById(R.id.time);
                messagecontent.setText(model.getText());
                messageuser.setText(model.getUser());
                //messagetime.setText(DateFormat.format("MM.DD.YYYY HH:mm:ss",model.getTime()));
            }
        };
        history.setAdapter(fbadapter);
    }
}


