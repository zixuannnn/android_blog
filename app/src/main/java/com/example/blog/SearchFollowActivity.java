package com.example.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.blog.Adapter.SearchUserAdapter;
import com.example.blog.Model.Post;
import com.example.blog.Model.UserDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFollowActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference ref, ref2, ref3;
    private List<UserDetail> listUser = new ArrayList<>();
    private List<Post> listPost = new ArrayList<>();
    private List<String> key = new ArrayList<>();
    private FirebaseDatabase database;
    private String name, uid;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_rv);
        recyclerView = (RecyclerView) findViewById(R.id.search_rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchFollowActivity.this));

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        if(intent.getStringExtra("search").equals("follower"))
            searchFollower(uid);
        else if(intent.getStringExtra("search").equals("following"))
            searchFollowing(uid);

    }

    private void searchFollower(String uid){
        ref = database.getReference("Users").child(uid).child("followerUsers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    key.add(user.getKey());
                }
                if(key.size() == 0){
                    adapter = new SearchUserAdapter(getApplicationContext(), listUser);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    for (int i = 0; i < key.size(); i++) {
                        ref2 = database.getReference("Users").child(key.get(i));
                        final int finalI = i;
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String uri = dataSnapshot.child("photo").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                String name = dataSnapshot.child("username").getValue(String.class);
                                String userid = dataSnapshot.child("id").getValue(String.class);
                                String pwd = dataSnapshot.child("password").getValue(String.class);
                                UserDetail u = new UserDetail(name, email, pwd, userid);
                                if (uri != null)
                                    u.setPhoto(Uri.parse(uri));
                                listUser.add(u);
                                if (finalI == key.size() - 1) {
                                    adapter = new SearchUserAdapter(getApplicationContext(), listUser);
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchFollowing(String uid){
        ref = database.getReference("Users").child(uid).child("followingUsers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    key.add(user.getKey());
                }
                if(key.size() == 0){
                    adapter = new SearchUserAdapter(getApplicationContext(), listUser);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    for (int i = 0; i < key.size(); i++) {
                        ref2 = database.getReference("Users").child(key.get(i));
                        final int finalI = i;
                        ref2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String uri = dataSnapshot.child("photo").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                String name = dataSnapshot.child("username").getValue(String.class);
                                String userid = dataSnapshot.child("id").getValue(String.class);
                                String pwd = dataSnapshot.child("password").getValue(String.class);
                                UserDetail u = new UserDetail(name, email, pwd, userid);
                                if (uri != null)
                                    u.setPhoto(Uri.parse(uri));
                                listUser.add(u);
                                if (finalI == key.size() - 1) {
                                    adapter = new SearchUserAdapter(getApplicationContext(), listUser);
                                    recyclerView.setAdapter(adapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
