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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.Adapter.CommentAdapter;
import com.example.blog.Model.Comment;
import com.example.blog.Model.Database;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference refPostComment, refUser, refPostLike, refPosts;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private LinearLayout linearLayout1, linearLayout2;
    private RelativeLayout relativeLayout;
    private ImageView post, userPhoto, like;
    private TextView username, intro;
    private RecyclerView rv;
    private EditText comment;
    private ImageButton postComment;
    private Intent intent;
    private String postId, userName, email, title, uri, uid;
    private List<Comment> list;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_detail);

        intent = getIntent();
        postId = intent.getStringExtra("postId");
        userName = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        title = intent.getStringExtra("title");
        uri = intent.getStringExtra("uri");

        database = FirebaseDatabase.getInstance();
        refUser = database.getReference("Users");

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);
        relativeLayout = linearLayout1.findViewById(R.id.relativeLayout);

        post = findViewById(R.id.postImage);
        userPhoto = relativeLayout.findViewById(R.id.userPhoto);
        username = relativeLayout.findViewById(R.id.userName);
        like = relativeLayout.findViewById(R.id.like);

        intro = linearLayout1.findViewById(R.id.intro);

        comment = linearLayout2.findViewById(R.id.commentEdit);
        postComment = linearLayout2.findViewById(R.id.postComment);

        rv = findViewById(R.id.comment_rv);
        list = new ArrayList<>();

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(PostDetailActivity.this));

        Picasso.get().load(uri).into(post);
        username.setText(userName);

        refUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user: dataSnapshot.getChildren()) {
                    if(user.child("email").getValue(String.class).equals(email)) {
                        uid = user.child("id").getValue(String.class);
                        if (user.child("photo").exists())
                            Picasso.get().load(user.child("photo").getValue(String.class)).into(userPhoto);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        refPosts = database.getReference("Posts").child(postId);
        refPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                intro.setText(dataSnapshot.child("intro").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postComment.setOnClickListener(new View.OnClickListener() {
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

        refPostComment = database.getReference("Posts").child(postId).child("Comments");
        refPostComment.addValueEventListener(new ValueEventListener() {
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
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        like(postId, like);

        refPostLike = database.getReference("Posts").child(postId).child("likes");
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refUser = database.getReference().child("Users").child(user.getUid());
                if(like.getTag().equals("like")) {
                    refPostLike.child(user.getUid()).setValue(user.getEmail());
                    refUser.child("likes").child(postId).setValue(title);
                }
                else{
                    refPostLike.child(user.getUid()).removeValue();
                    refUser.child("likes").child(postId).removeValue();
                }
            }
        });

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OthersProfilePageActivity.class);
                intent.putExtra("user", userName);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OthersProfilePageActivity.class);
                intent.putExtra("user", userName);
                intent.putExtra("email", email);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });


    }

    private void addComment(FirebaseUser user, String postId, String comment, Date date) {
        Comment c = new Comment(comment, user.getEmail(), date);
        Database d = Database.getDatabase();
        d.saveToComment(c, postId, getApplicationContext());
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void like(String postId, final ImageView liked){
        refPosts = database.getReference().child("Posts").child(postId).child("likes");

        refPosts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(user != null && dataSnapshot.child(user.getUid()).exists()) {
                    liked.setImageResource(R.drawable.ic_liked);
                    liked.setTag("liked");
                }
                else{
                    liked.setImageResource(R.drawable.ic_like);
                    liked.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}