package com.example.blog.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blog.Adapter.PostListAdapter;
import com.example.blog.Model.Post;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchPostFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostListAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private List<Post> listPost;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.post_in_profile_rv, container, false);
        recyclerView = view.findViewById(R.id.profile_rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        Bundle bundle = getArguments();
        list = bundle.getStringArrayList("post");
        listPost = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ref = database.getReference("Posts");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPost.clear();
                for(String i : list){
                    String url = dataSnapshot.child(i).child("imageUrl").getValue(String.class);
                    String name = dataSnapshot.child(i).child("name").getValue(String.class);
                    String intro = dataSnapshot.child(i).child("intro").getValue(String.class);
                    String email = dataSnapshot.child(i).child("mEmail").getValue(String.class);
                    String key = dataSnapshot.child(i).child("postKey").getValue(String.class);
                    Date date = dataSnapshot.child(i).child("mTime").getValue(Date.class);
                    Post p = new Post(name, url, key, email, date, intro);
                    listPost.add(p);
                }
                adapter = new PostListAdapter(getContext(), listPost);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }
}
