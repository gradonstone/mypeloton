package edu.fsu.cs.mobile.mypeloton;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    Button register, login;
    EditText email, password;

    public static final int RC_SIGN_IN = 123;
    SignInButton googleLogin;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.register_button);
        login = findViewById(R.id.login_button);
        email = findViewById(R.id.username_edit);
        password = findViewById(R.id.pass_edit);


        //----------------For google sign in --------------------

        googleLogin = (SignInButton) findViewById(R.id.google_login_button);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // mGoogleSignInClient.signOut();

        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Main Activity", "Pressed Login Button");
                boolean is_empty = true;
                if (email.getText().toString() == "")
                    is_empty = false;
                else if (password.getText().toString() == "")
                    is_empty = false;

                if (!is_empty)
                    Toast.makeText(MainActivity.this, "Please Enter both Email and Password", Toast.LENGTH_SHORT).show();
                else {
                    signInWithEmail(email.getText().toString(), password.getText().toString());
                }
            }

        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Main Activity", "Pressed Register Button");
                boolean is_empty = true;
                if (email.getText().toString() == "")
                    is_empty = false;
                else if (password.getText().toString() == "")
                    is_empty = false;

                if (!is_empty)
                    Toast.makeText(MainActivity.this, "Please Enter both Email and Password", Toast.LENGTH_SHORT).show();
                else {
                    createAccountWithEmail(email.getText().toString(), password.getText().toString());
                }
            }
        });

    }

    private void createAccountWithEmail(String email, String password) {

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Main Activity", "Create Email User Successful");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                                myIntent.putExtra("uid", user.getUid());
                                myIntent.putExtra("email", user.getEmail());
                                startActivity(myIntent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Main Activity", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
    }

    private void signInWithEmail(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("Main Activity", "Sign in Successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                            myIntent.putExtra("uid", user.getUid());
                            myIntent.putExtra("email", user.getEmail());
                            startActivity(myIntent);
                        }
                        else {
                            Log.w("Main Activity", "Login with Email failed");
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("Main Activity", "Google sign in failed", e);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Main Activity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Main Activity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                            myIntent.putExtra("uid", user.getUid());
                            myIntent.putExtra("email", user.getEmail());
                            startActivity(myIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Main Activity", "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            // FirebaseAuth.getInstance().signOut();
            // mGoogleSignInClient.signOut();
            updateUI(currentUser);
        }
    }

    // updates the UI to SearchActivity with the firebase user

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            // FirebaseAuth.getInstance().signOut();
            Intent mIntent =  new Intent(MainActivity.this, SearchActivity.class);
            mIntent.putExtra("uid", user.getUid());
            mIntent.putExtra("email",user.getEmail());
            startActivity(mIntent);
        }
    }
}
