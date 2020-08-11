package com.example.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.blog.Model.UserDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class RegisterActivity extends AppCompatActivity {
    private Button register;
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirm;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Uri photo;
    ImageView imgUserPhoto;
    private FirebaseDatabase database;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        register = (Button)findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);      // store username into database Later
        email   = (EditText)findViewById(R.id.email);
        password   = (EditText)findViewById(R.id.password);
        confirm = (EditText)findViewById(R.id.confirm);
        imgUserPhoto = (ImageView) findViewById(R.id.image);

        register.setOnClickListener(            // RegisterActivity Click
                new View.OnClickListener() {
                    public void onClick(View view){
                        // Finish RegisterActivity, Go to Home page
                        if(password.getText().toString().equals(confirm.getText().toString()))
                            creatAccount(email.getText().toString(), password.getText().toString(), username.getText().toString());
                        else
                            showMessage("Confirmed Password is not the same as Password");
                    }
                }
        );
    }

    private void creatAccount(final String mail, final String pwd, final String username) {

        mAuth.createUserWithEmailAndPassword(mail, pwd)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Created Successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateInfo(username, pikedImage, mAuth.getCurrentUser());
                            saveIntoDatabase(user.getUid(), username, mail, pwd);
                            updateUIHome(user);
                        }
                        else {
                            showMessage("Account created failed");
                        }

                    }
                });

    }

//    private void updateInfo(final String username, Uri pikedImage, final FirebaseUser user) {
//        StorageReference mStorage = storage.getReference().child("users_photo");
//        final StorageReference imageFilePath = mStorage.child(pikedImage.getLastPathSegment());
//        imageFilePath.putFile(pikedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // Image upload successfully
//                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        //Uri contains user image url
//
//                        UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(username).setPhotoUri(uri).build();
//                        user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()){
//                                    showMessage("RegisterActivity Successfully");
//                                }
//                                else {
//                                    showMessage("RegisterActivity Failed");
//                                }
//                            }
//                        });
//                    }
//                });
//            }
//        });

//    }

    private void saveIntoDatabase(String id, String username, String email, String password){
        database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(id);
        UserDetail user = new UserDetail(username, email, password, id);
        ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showMessage("Stored Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Stored Failed");
                System.out.println("=====================");
                System.out.println(e);
                System.out.println("=====================");
            }
        });

        finish();
    }

    private void updateUIHome(FirebaseUser user){
        if (user == null)
            return;
        Intent intent = new Intent(RegisterActivity.this, PostListActivity.class);
        startActivity(intent);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
