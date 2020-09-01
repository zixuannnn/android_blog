package com.example.blog.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.blog.Adapter.LikeCardAdapter;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewLikeFragment extends Fragment {

    private RecyclerView recyclerView;
    private LikeCardAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ref, ref2;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private List<Map<String, String>> list;
    private List<String> key;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.recyclerview_in_message_page, container, false);
        recyclerView = view.findViewById(R.id.message_rv);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        key = new ArrayList<>();

        ref = database.getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //key.clear();
                Map<String, String> map = new HashMap<>();
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    if(post.child("mEmail").getValue(String.class).equals(currentUser.getEmail())){
                        for(DataSnapshot s : post.child("likes").getChildren()){
                            if(!s.getValue(Boolean.class)){
                                map.put("uid",s.getKey());
                                map.put("date",post.child("likesDate").child(s.getKey()).getValue(String.class));
                                map.put("title", post.child("name").getValue(String.class));
                                map.put("email", post.child("mEmail").getValue(String.class));
                                map.put("postId",post.getKey());
                                map.put("post", post.child("imageUrl").getValue(String.class));
                                list.add(map);
                                key.add(s.getKey());
                                map = new HashMap<>();
                            }
                        }
                    }
                }
                if(key.size() == 0){
                    adapter = new LikeCardAdapter(getContext(), list);
                    recyclerView.setAdapter(adapter);
                }
                else{
                    for(int i=0; i<key.size(); i++){
                        final int keyIndex = i;
                        for(int j=0; j<list.size(); j++){
                            if(list.get(j).get("uid").equals(key.get(i))){
                                final int index = j;
                                ref2 = database.getReference("Users");
                                ref2.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        list.get(index).put("username",dataSnapshot.child(key.get(keyIndex)).child("username").getValue(String.class));
                                        list.get(index).put("photo", dataSnapshot.child(key.get(keyIndex)).child("photo").getValue(String.class));
                                        list.get(index).put("authorUsername", dataSnapshot.child(currentUser.getUid()).child("username").getValue(String.class));

                                        if (index == key.size() - 1) {
                                            adapter = new LikeCardAdapter(getContext(), list);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void showMessage(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }
}
