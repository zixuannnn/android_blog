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

import com.example.blog.Adapter.SearchUserAdapter;
import com.example.blog.Model.UserDetail;
import com.example.blog.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private SearchUserAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private List<UserDetail> listUser;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.post_in_profile_rv, container, false);
        recyclerView = view.findViewById(R.id.profile_rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Bundle bundle = getArguments();
        list = bundle.getStringArrayList("user");
        listUser = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        ref = database.getReference("Users");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUser.clear();
                for(String i : list){
                    String url = dataSnapshot.child(i).child("photo").getValue(String.class);
                    String name = dataSnapshot.child(i).child("username").getValue(String.class);
                    String key = dataSnapshot.child(i).child("id").getValue(String.class);
                    String email = dataSnapshot.child(i).child("email").getValue(String.class);
                    String pwd = dataSnapshot.child(i).child("password").getValue(String.class);
                    UserDetail u = new UserDetail(name, email, pwd, key);
                    if(url != null)
                        u.setPhoto(Uri.parse(url));
                    listUser.add(u);
                }
                adapter = new SearchUserAdapter(getContext(), listUser);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }
}

