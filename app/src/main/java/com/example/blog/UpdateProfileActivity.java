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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText username, birthday;
    private TextView id;
    private ImageView photo;
    private Button update;
    private Uri uri;
    private FirebaseDatabase database;
    private DatabaseReference Dref;
    private StorageReference Sref;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Intent intent;

    private final static int REQUESCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile);

        username = findViewById(R.id.edit_name);
        birthday = findViewById(R.id.edit_birth);
        id = findViewById(R.id.show_id);
        update = findViewById(R.id.update);
        photo = findViewById(R.id.photo);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        id.setText(user.getUid());

        Dref = database.getReference("Users").child(user.getUid());
        Dref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue(String.class));
                Picasso.get().load(dataSnapshot.child("photo").getValue(String.class)).into(photo);
                birthday.setText(dataSnapshot.child("birthday").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString() != null)
                    database.getReference("Users").child(user.getUid()).child("username").setValue(username.getText().toString());
                if(birthday.getText().toString() != null)
                    database.getReference("Users").child(user.getUid()).child("birthday").setValue(birthday.getText().toString());

                updateToProfileUI();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESCODE && resultCode == RESULT_OK && data != null){
            uri = data.getData();
            photo.setImageURI(uri);
            Picasso.get().load(uri).into(photo);
            savePhotoStorage();
        }
    }

    private void savePhotoStorage(){
        storage = FirebaseStorage.getInstance();
        final StorageReference imagePath = storage.getReference().child("blogImages").child(uri.getLastPathSegment());
        imagePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        savePhotoDatabase(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Get uri failed ");
                        System.out.println("==============");
                        System.out.println(e);
                        System.out.println("==============");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Upload to storage failed");
                System.out.println("==============");
                System.out.println(e);
                System.out.println("==============");
            }
        });

    }

    private void savePhotoDatabase(final Uri uri){
        database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("Users").child(user.getUid()).child("photo");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(uri.toString());
                showMessage("Photo Saved Successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToProfileUI(){
        Intent intent = new Intent(UpdateProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
