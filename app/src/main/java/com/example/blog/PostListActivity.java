package com.example.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.blog.Adapter.PostListAdapter;
import com.example.blog.Model.Post;
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

public class PostListActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference ref;
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private List<Post> list = new ArrayList<>();
    private BottomNavigationView bottomNavigation;
    private Toolbar toolbar;
    private EditText searchField;
    private SearchView search;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_list_recycler_view);
        recyclerView = (RecyclerView)findViewById(R.id.rv);
        toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);

        searchField = toolbar.findViewById(R.id.searchField);
        search = toolbar.findViewById(R.id.search);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(manager);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        //ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        search.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = searchField.getText().toString();
                list.clear();
                if(username.isEmpty()){
                    showMessage("Please specify your search condition");
                }
                else {
                    Intent intent = new Intent(PostListActivity.this, SearchActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });

        ref =  database.getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot post : dataSnapshot.getChildren()){
                    String url = post.child("imageUrl").getValue(String.class);
                    String name = post.child("name").getValue(String.class);
                    String key = post.child("postKey").getValue(String.class);
                    String email = post.child("mEmail").getValue(String.class);
                    Date time = post.child("mTtime").getValue(Date.class);
                    String intro = post.child("intro").getValue(String.class);
                    Post p = new Post(name, url, key, email, time, intro);
                    list.add(p);
                }

                adapter = new PostListAdapter(getApplicationContext(), list);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.Home){
            updatePostListUI();
        }
        else if(id == R.id.add){
            updatePostUI();
        }
        else if(id == R.id.Me){
            updateProfileUI();
        }
        else if(id == R.id.Message){
            updateMessageUI();
        }

        return true;
    }

    private void updatePostUI() {
        if(currentUser != null) {
            Intent intent = new Intent(PostListActivity.this, PopUpActivity.class);
            startActivity(intent);
        }
        else {
            updateLoginUI();
            showMessage("Sorry you have not signin yet");
        }
    }

    private void updateLoginUI(){
        if(currentUser == null) {
            Intent intent = new Intent(PostListActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            showMessage("Sorry, you are currently signin with "+currentUser.getEmail());
        }
    }

    private void updateMessageUI(){
        if(currentUser == null) {
            Intent intent = new Intent(PostListActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(PostListActivity.this, MessagePageActivity.class);
            startActivity(intent);
        }
    }

    private void updateProfileUI(){
        if(currentUser == null) {
            Intent intent = new Intent(PostListActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent = new Intent(PostListActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void updatePostListUI(){
        if(currentUser != null){
            Intent intent = new Intent(PostListActivity.this, PostListActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(PostListActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}
