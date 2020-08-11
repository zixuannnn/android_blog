package com.example.blog;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.blog.Adapter.CommentAdapter;
import com.example.blog.Model.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference ref;
    private List<Comment> list = new ArrayList<>();
    private ImageButton post;
    private String postId;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private EditText comment;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_recycler_view);
        recyclerView = (RecyclerView)findViewById(R.id.comment_rv);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        comment = findViewById(R.id.commentEdit);
        post = findViewById(R.id.postComment);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentActivity.this));

        Intent intent = getIntent();
        postId = intent.getStringExtra("postid");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getText().toString().isEmpty()){
                    showMessage("Please type your comment");
                }
                else {
                    Date currentTime = Calendar.getInstance().getTime();
                    addComment(user, postId, comment.getText().toString(), currentTime);
                }
            }
        });

        ref =  FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    String publisher = post.child("publisher").getValue(String.class);
                    String comment = post.child("comment").getValue(String.class);
                    Date time = post.child("date").getValue(Date.class);
                    Comment c = new Comment(comment, publisher, time);
                    list.add(c);
                }

                adapter = new CommentAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addComment(FirebaseUser user, String postId, String comment, Date date) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child("Posts").child(postId).child("Comments").push();

        Comment c = new Comment(comment, user.getEmail(), date);
//        map.put("comment", comment);
//        map.put("publisher", user.getEmail());
//        map.put("userId", user.getUid());
        ref.setValue(c).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Stored Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Stored Failed");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


}
