package com.example.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blog.Model.Database;
import com.example.blog.Model.UserDetail;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity {
    private Button login, register, googleOath;
    private EditText email, password;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    //final FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = (Button)findViewById(R.id.login);
        googleOath = findViewById(R.id.googleOauth);
        register = (Button)findViewById(R.id.register);
        email   = (EditText)findViewById(R.id.email);
        password   = (EditText)findViewById(R.id.password);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        database = FirebaseDatabase.getInstance();

        login.setOnClickListener(               // LoginActivity click
                new View.OnClickListener() {
                public void onClick(View view) {
                        final String mail = email.getText().toString();
                        final String pwd = password.getText().toString();

                        if(mail.isEmpty() || pwd.isEmpty()){
                            showMessage("Please fullfill all info that in need");
                        }
                        else {
                            // login, go to user page
                            login(mail, pwd);
                        }
                    }
                });

        googleOath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        register.setOnClickListener(            // RegisterActivity Click
                new View.OnClickListener() {
                    public void onClick(View view){
                        // Go to register page
                        updateUIRegister();
                    }
                }
        );
    }

    public void login(final String email, final String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMessage("LoginActivity Successfully");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUILogin(user);
                        } else {
                            showMessage("LoginActivity Failed");
                        }
                    }
                });
    }

    public void updateUILogin(FirebaseUser user){
        if (user == null)
            return;
        Intent intent = new Intent(LoginActivity.this, PostListActivity.class);
        startActivity(intent);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                showMessage(e.toString());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            ref = database.getReference("Users");
                            Query query = ref.orderByChild("email").equalTo(user.getEmail());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(!dataSnapshot.exists()){
                                        UserDetail u = new UserDetail(null, user.getEmail(), null, user.getUid());
                                        Database d = Database.getDatabase();
                                        d.saveToUserDetail(u, user.getUid(), getApplicationContext());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            updateUILogin(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            showMessage("Login Failed");
                        }
                    }
                });
    }

    private void updateUIRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}


