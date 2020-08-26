package com.example.blog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blog.Fragment.LikeFragment;
import com.example.blog.Fragment.PostFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener{

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private Uri photoUri;
    private ImageView photo1, photo2;
    private FirebaseDatabase database;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;
    private LinearLayout linear1, child_linear1, child_linear2, child_linear11;
    private CardView card1, card2;
    private View header;
    private Bundle bundle;
    private FirebaseStorage storage;
    private DrawerLayout drawer;
    private TextView username, email, following, follower;
    private Button updateProfile;

    private final static int REQUESCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        navigationView = findViewById(R.id.nav_profile);
        header = navigationView.getHeaderView(0);

        linear1 = findViewById(R.id.profile_linear1);
        child_linear1 = linear1.findViewById(R.id.child_linear1);
        child_linear11 = child_linear1.findViewById(R.id.child_linear11);
        child_linear2 = linear1.findViewById(R.id.child_linear2);
        card1 = header.findViewById(R.id.card);
        card2 = linear1.findViewById(R.id.card);

        photo1 = card1.findViewById(R.id.userPhoto);
        photo2 = card2.findViewById(R.id.photo);

        username = child_linear11.findViewById(R.id.username);
        email = child_linear11.findViewById(R.id.email_profile);
        follower = child_linear2.findViewById(R.id.profile_followers);
        following = child_linear2.findViewById(R.id.profile_followings);

        updateProfile = child_linear2.findViewById(R.id.updateProfile);

        storage = FirebaseStorage.getInstance();

        drawer = findViewById(R.id.drawer_profile);

        bottomNavigation = findViewById(R.id.profile_nav);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(listener);

        if(mAuth != null && currentUser != null){
            updateNavigationBar();
            updateLinearLayout();
        }

        bundle = new Bundle();
        bundle.putString("uid", currentUser.getUid());
        bundle.putString("email", currentUser.getEmail());
        Fragment fragment = new PostFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        photo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser == null) {
                    showMessage("Please Login First");
                }
                else {
                    openGallery();
                }
            }
        });

        photo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentUser == null) {
                    showMessage("Please Login First");
                }
                else {
                    openGallery();
                }
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateToUpdateUI();
            }
        });

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ProfileActivity.this, SearchFollowActivity.class);
                intent2.putExtra("uid", currentUser.getUid());
                intent2.putExtra("search", "follower");
                startActivity(intent2);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ProfileActivity.this, SearchFollowActivity.class);
                intent2.putExtra("uid", currentUser.getUid());
                intent2.putExtra("search", "following");
                startActivity(intent2);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Bundle bundle = new Bundle();
            bundle.putString("uid", currentUser.getUid());
            bundle.putString("email", currentUser.getEmail());
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            updatePostListUI();     // Go to PostListActivity.java

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_profile) {

        }
        else if (id == R.id.nav_post) {
            updatePostUI();     // Go to PopUpActivity.java post picture

        } else if (id == R.id.nav_logout) {
            handleFirebaseLogout();     // Firebase Logout
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void updatePostUI() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Intent intent = new Intent(ProfileActivity.this, PopUpActivity.class);
            startActivity(intent);
        }
        else {
            updateLoginUI();
            showMessage("Sorry you have not signin yet");
        }
    }

    private void updateLoginUI(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else{
            showMessage("Sorry, you are currently signin with "+user.getEmail());
        }
    }

    private void updatePostListUI(){
        Intent intent = new Intent(ProfileActivity.this, PostListActivity.class);
        startActivity(intent);
    }

    private void updateToUpdateUI(){
        Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
//        intent.putExtra("uri", photoUri.toString());
//        intent.putExtra("username", username.getText().toString());
        startActivity(intent);
    }

    private void handleFirebaseLogout() {
        FirebaseAuth.getInstance().signOut();
        finish();
        showMessage("Successfully Logout");
    }

    private void updateNavigationBar(){
        DatabaseReference ref = database.getReference("Users").child(currentUser.getUid());
        final TextView username = header.findViewById(R.id.username);
        final TextView email = header.findViewById(R.id.email);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue(String.class));
                email.setText(dataSnapshot.child("email").getValue(String.class));
                String uri = dataSnapshot.child("photo").getValue(String.class);
                if(uri != null ){
                    Picasso.get().load(uri).into(photo1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateLinearLayout(){
        DatabaseReference ref = database.getReference("Users").child(currentUser.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username.setText(dataSnapshot.child("username").getValue(String.class));
                email.setText(dataSnapshot.child("email").getValue(String.class));
                follower.setText(dataSnapshot.child("follower").getValue(int.class)+" followers\t");
                following.setText(dataSnapshot.child("following").getValue(int.class)+" followings\t");
                String uri = dataSnapshot.child("photo").getValue(String.class);
                if(uri != null ){
                    Picasso.get().load(uri).into(photo2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, REQUESCODE);
    }

    private void savePhotoStorage(){
        storage = FirebaseStorage.getInstance();
        final StorageReference imagePath = storage.getReference().child("blogImages").child(photoUri.getLastPathSegment());
        imagePath.putFile(photoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        savePhotoDatabase(uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Get uri failed ");
                        System.out.println("==============");
                        System.out.println(e);
                        System.out.println("==============");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage("Upload to storage failed");
                System.out.println("==============");
                System.out.println(e);
                System.out.println("==============");
            }
        });

    }

    private void savePhotoDatabase(final Uri uri){
        database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("Users").child(currentUser.getUid()).child("photo");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().setValue(uri.toString());
                showMessage("Photo Saved Successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESCODE && resultCode == RESULT_OK && data != null){
            photoUri = data.getData();
            photo1.setImageURI(photoUri);
            Picasso.get().load(photoUri).into(photo1);
            savePhotoStorage();
        }
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}

