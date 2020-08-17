package com.example.blog.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.blog.Adapter.PostListAdapter;
import com.example.blog.Model.Post;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LikeFragment extends Fragment {

    private RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private PostListAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private List<Post> list;
    private String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.likes_in_profile_rv, container, false);
        recyclerView = view.findViewById(R.id.profile_rv);

        Bundle bundle = getArguments();
        uid = bundle.getString("uid");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        list = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        ref = database.getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    if(post.child("likes").child(uid).exists()){
                        String url = post.child("imageUrl").getValue(String.class);
                        String name = post.child("name").getValue(String.class);
                        String key = post.child("postKey").getValue(String.class);
                        String email = post.child("mEmail").getValue(String.class);
                        Date time = post.child("mTtime").getValue(Date.class);
                        String intro = post.child("intro").getValue(String.class);
                        Post p = new Post(name, url, key, email, time, intro);
                        list.add(p);
                    }
                }
                adapter = new PostListAdapter(getContext(), list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }
}
