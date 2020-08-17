package com.example.blog.Model;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Database {
    private static Database database;
    private FirebaseDatabase fd = FirebaseDatabase.getInstance();
    private DatabaseReference fRef;
    private FirebaseStorage fs = FirebaseStorage.getInstance();
    private StorageReference sRef = fs.getReference();

    private Database(){
        // Singleton
    }

    public static Database getDatabase(){
        if(database == null){
            database = new Database();
        }
        return database;
    }

    public void saveToUserDetail(UserDetail u, String id, final Context context){
        fRef = fd.getReference("Users").child(id);
        fRef.setValue(u).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("User Saved Successfully", context);
            }

//            @Override
//            public void onSuccess(@NonNull Task<Void> task) {
//                showMessage("User Saved Successfully", context);
//            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("User Saved Failed", context);
                System.out.println("=====================");
                System.out.println(e);
                System.out.println("=====================");
            }
        });

    }

    public void savePostToStorageAndDatabase(Uri uri, final Post p, final Context context){
        final StorageReference imageFilePath = sRef.child("blogImages").child(uri.getLastPathSegment());
        imageFilePath.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                fRef = fd.getReference("Posts").push();
                                String uploadId = fRef.getKey();
                                //Post upload = new Post(title.getText().toString(), uri.toString(), uploadId, user.getEmail(), currentTime);
                                p.setPostKey(uploadId);
                                p.setImageUrl(uri.toString());
                                fRef.setValue(p).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showMessage("Upload to database Failed!", context);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                showMessage("Upload Failed", context);
                                System.out.println("=====================");
                                System.out.println(e.toString());
                                System.out.println("=====================");
                            }
                        });
                    }
                });
    }

    public void saveToComment(Comment c, String postId, final Context context){
        fRef = fd.getReference("Posts");
        fRef = fRef.child(postId).child("Comments").push();
        fRef.setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Stored Successfully", context);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Stored Failed", context);
            }
        });
    }

    private void showMessage(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}
