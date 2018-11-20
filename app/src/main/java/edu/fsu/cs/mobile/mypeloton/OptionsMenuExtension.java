package edu.fsu.cs.mobile.mypeloton;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OptionsMenuExtension extends AppCompatActivity {

    private AuthCredential credential;

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
                Intent mIntent = new Intent(getBaseContext(), MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(mIntent);
                finish();
                break;
            }
            case R.id.delete_account:
            {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final Intent mIntent = new Intent(getBaseContext(), MainActivity.class);
                if(user != null)
                {
                    String idToken = user.getIdToken(true).toString();
                    if(idToken == null)
                        credential = EmailAuthProvider.getCredential(user.getEmail(), null);
                    else
                        credential = GoogleAuthProvider.getCredential(idToken, null);

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference().child("requests").orderByChild("userID").equalTo(user.getUid())
                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                                    snapshot.getRef().removeValue();
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                            }
                                                        });
                                                startActivity(mIntent);
                                            }
                                            else
                                                task.getException();
                                        }
                                    });
                                }
                            });
                }
            }
        }
        return true;
    }
}
