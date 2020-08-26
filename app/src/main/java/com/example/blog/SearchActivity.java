package com.example.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.blog.Fragment.SearchPostFragment;
import com.example.blog.Fragment.SearchUserFragment;
import com.example.blog.Model.Post;
import com.example.blog.Model.UserDetail;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference ref, ref2, ref3;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> listPosts = new ArrayList<>();
    private List<UserDetail> listUser = new ArrayList<>();
    private List<Post> listPost = new ArrayList<>();
    private List<String> key = new ArrayList<>();
    private FirebaseDatabase database;
    private String name, uid;
    private BottomNavigationView bottomNavigation;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_recycler_view);
        bottomNavigation = findViewById(R.id.search_nav);

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        name = intent.getStringExtra("username");
        if(name != null) {
            searchUser(name);
            searchPosts(name);
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Bundle bundlePost = new Bundle();
            bundlePost.putStringArrayList("post", listPosts);
            Fragment fragment = new SearchPostFragment();
            fragment.setArguments(bundlePost);

            if(menuItem.getItemId() == R.id.post){
                fragment = new SearchPostFragment();
                fragment.setArguments(bundlePost);
            }
            else if(menuItem.getItemId() == R.id.user){
                Bundle bundleUser = new Bundle();
                bundleUser.putStringArrayList("user", list);
                fragment = new SearchUserFragment();
                fragment.setArguments(bundleUser);
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
    };

    private void searchUser(String username){
        ref = database.getReference("Users");
        Query query = ref.orderByChild("username").startAt(username).endAt(username+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    list.add(user.child("id").getValue(String.class));
                }
                bottomNavigation.setOnNavigationItemSelectedListener(listener);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showMessage("Search Error");
            }
        });
    }

    private void searchPosts(String title){
        ref3 = database.getReference("Posts");
        Query query = ref3.orderByChild("name").startAt(title).endAt(title+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    listPosts.add(user.child("postKey").getValue(String.class));
                }
                bottomNavigation.setOnNavigationItemSelectedListener(listener);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}
