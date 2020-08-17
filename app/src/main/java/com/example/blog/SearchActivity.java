package com.example.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.blog.Adapter.SearchUserAdapter;
import com.example.blog.Model.UserDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference ref;
    private List<UserDetail> list = new ArrayList<>();
    private FirebaseDatabase database;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_recycler_view);
        recyclerView = (RecyclerView) findViewById(R.id.search_rv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        searchUser(username);


    }

    private void searchUser(String username){
        ref = database.getReference("Users");
        Query query = ref.orderByChild("username").startAt(username).endAt(username+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    String url = user.child("photo").getValue(String.class);
                    String name = user.child("username").getValue(String.class);
                    String key = user.child("id").getValue(String.class);
                    String email = user.child("email").getValue(String.class);
                    String pwd = user.child("password").getValue(String.class);
                    UserDetail u = new UserDetail(name, email, pwd, key);
                    if(url != null)
                        u.setPhoto(Uri.parse(url));
                    list.add(u);
                }

                adapter = new SearchUserAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Search Error");
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
