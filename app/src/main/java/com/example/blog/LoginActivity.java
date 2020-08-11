package com.example.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.storage.FirebaseStorage;

public class LoginActivity extends AppCompatActivity {
    private Button login, register;
    private EditText email, password;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //final FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        login = (Button)findViewById(R.id.login);
        register = (Button)findViewById(R.id.register);
        email   = (EditText)findViewById(R.id.email);
        password   = (EditText)findViewById(R.id.password);

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

    private void updateUIRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}


