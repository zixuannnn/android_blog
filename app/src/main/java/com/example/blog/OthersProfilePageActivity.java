package com.example.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.Fragment.LikeFragment;
import com.example.blog.Fragment.PostFragment;
import com.example.blog.Notification.Observer;
import com.example.blog.Notification.ObserverAction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OthersProfilePageActivity extends AppCompatActivity {

    private ImageView photo;
    private FirebaseDatabase database;
    private BottomNavigationView bottomNavigation;
    private LinearLayout linear1, child_linear1, child_linear2, child_linear11;
    private TextView username, email, following, follower;
    private Button follow;
    private Intent intent;
    private Bundle bundle;
    private Observer observer;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_profile);

        intent = getIntent();

        observer = new ObserverAction();

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        linear1 = findViewById(R.id.profile_linear1);
        child_linear1 = linear1.findViewById(R.id.child_linear1);
        child_linear11 = child_linear1.findViewById(R.id.child_linear11);
        child_linear2 = linear1.findViewById(R.id.child_linear2);

        photo = child_linear1.findViewById(R.id.photo);

        username = child_linear11.findViewById(R.id.username);
        email = child_linear11.findViewById(R.id.email_profile);
        follower = child_linear2.findViewById(R.id.profile_followers);
        following = child_linear2.findViewById(R.id.profile_followings);
        follow = child_linear2.findViewById(R.id.follow);

        bottomNavigation = findViewById(R.id.profile_nav);

        bottomNavigation.setOnNavigationItemSelectedListener(listener);

        updateLinearLayout();

        bundle = new Bundle();
        bundle.putString("uid", intent.getStringExtra("uid"));
        bundle.putString("email", intent.getStringExtra("email"));
        Fragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observer.NotificationAfterNewFollow(currentUser.getEmail()+" is following you now",getApplicationContext(), intent.getStringExtra("uid"));
                observer.IncreaseFollowingFollower(currentUser.getUid(), intent.getStringExtra("uid"), intent.getStringExtra("email"), currentUser.getEmail());
                updateLinearLayout();
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment fragment = new PostFragment();
            fragment.setArguments(bundle);

            if(menuItem.getItemId() == R.id.Post){
                fragment = new PostFragment();
                fragment.setArguments(bundle);
            }
            else if(menuItem.getItemId() == R.id.Like){
                fragment = new LikeFragment();
                fragment.setArguments(bundle);
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
    };

    private void updateLinearLayout(){
        DatabaseReference ref = database.getReference("Users").child(intent.getStringExtra("uid"));

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue(String.class));
                email.setText(dataSnapshot.child("email").getValue(String.class));
                follower.setText(dataSnapshot.child("follower").getValue(int.class)+" followers\t");        //  Feature Will Be Added Later
                following.setText(dataSnapshot.child("following").getValue(int.class)+" followings\t");      //  Feature Will Be Added Later
                String uri = dataSnapshot.child("photo").getValue(String.class);
                if(uri != null ){
                    Picasso.get().load(uri).into(photo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        return false;
//    }
}
