package com.example.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.blog.Model.Post;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

public class PopUpActivity extends AppCompatActivity{
    private ImageButton post;
    private Button search;
    private ImageView picture;
    private EditText title;
    private ProgressBar progress;
    private FirebaseUser user;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private static final int REQUESCODE = 1;

    FirebaseStorage storage;
    FirebaseDatabase database;
    StorageReference storageReference;
    DatabaseReference databaseRef;
    private Uri pickedImgUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_post);

        // get the Firebase  storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();

        user = mAuth.getCurrentUser();

        search = (Button)findViewById(R.id.search);
        post = (ImageButton) findViewById(R.id.post);
        title = (EditText)findViewById(R.id.title);
        picture = (ImageView)findViewById(R.id.picture);
        progress = (ProgressBar)findViewById(R.id.progress);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                if(!title.getText().toString().isEmpty() && pickedImgUri
                        != null){
                    uploadFile();
                    updateHomeUI();
                    showMessage("Upload successfully");
                }
                else {
                    showMessage("Please check if all fileds already fullfiled");
                }
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            // Select pictures to be posted
            @Override
            public void onClick(View v) {
                // checkPermission();
                openGallery();
            }
        });

    }

    private void updateHomeUI() {
        Intent intent = new Intent(PopUpActivity.this, PostListActivity.class);
        startActivity(intent);
    }

//    private void checkPermission(){
//
//        if(ContextCompat.checkSelfPermission(PopUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED){
//
//            if(ActivityCompat.shouldShowRequestPermissionRationale(PopUpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
//                showMessage("Please accept for required permission");
//            }
//            else{
//                ActivityCompat.requestPermissions(PopUpActivity.this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PReqCode );
//            }
//        }
//        else {
//            showMessage("Everything goes well");
//            openGallery();
//        }
//
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESCODE && resultCode == RESULT_OK && data != null){
            pickedImgUri = data.getData();
            picture.setImageURI(pickedImgUri);
            Picasso.get().load(pickedImgUri).into(picture);

        }
    }

    private void openGallery() {

        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUESCODE);
    }

    private void uploadFile() {
        StorageReference fileReference = storageReference.child("blogImages");
        final StorageReference imageFilePath = fileReference.child(pickedImgUri.getLastPathSegment());
        final Date currentTime = Calendar.getInstance().getTime();
        imageFilePath.putFile(pickedImgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseRef = database.getReference("Posts").push();
                                String uploadId = databaseRef.getKey();
                                Post upload = new Post(title.getText().toString(), uri.toString(), uploadId, user.getEmail(), currentTime);
                                databaseRef.setValue(upload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage("Upload to database Failed!");
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Upload Failed");
                                System.out.println("=====================");
                                System.out.println(e.toString());
                                System.out.println("=====================");
                            }
                        });
                    }
                });

    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
