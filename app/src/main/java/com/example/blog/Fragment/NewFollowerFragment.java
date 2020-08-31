package com.example.blog.Fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.blog.Adapter.SearchUserAdapter;
import com.example.blog.Model.UserDetail;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewFollowerFragment extends Fragment {

    private RecyclerView recyclerView;
    private FrameLayout frameLayout;
    private SearchUserAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ref, ref2;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private List<UserDetail> list;
    private List<String> key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.recyclerview_in_message_page, container, false);
        recyclerView = view.findViewById(R.id.message_rv);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        key = new ArrayList<>();
        ref = database.getReference("Users").child(currentUser.getUid()).child("followerUsers");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot u : dataSnapshot.getChildren()){
                    if(!u.getValue(boolean.class)){
                        key.add(u.getKey());
                        //database.getReference("Users").child(currentUser.getUid()).child("followerUsers").child(u.getKey()).setValue(true);
                    }
                }
                if(key.size() == 0){
                    adapter = new SearchUserAdapter(getContext(), list);
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
                                list.add(u);
                                if (finalI == key.size() - 1) {
                                    adapter = new SearchUserAdapter(getContext(), list);
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

        return view;
    }
}
