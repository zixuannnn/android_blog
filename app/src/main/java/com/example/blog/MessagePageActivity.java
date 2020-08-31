package com.example.blog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.blog.Fragment.NewFollowerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MessagePageActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FrameLayout frameLayout;
    private BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_page);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        frameLayout = findViewById(R.id.fragmentContainer);
        nav = findViewById(R.id.message_nav);

        nav.setOnNavigationItemSelectedListener(listener);

        Fragment fragment = new NewFollowerFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            Fragment fragment = new NewFollowerFragment();

            if(menuItem.getItemId() == R.id.follow){
                fragment = new NewFollowerFragment();
            }
            else if(menuItem.getItemId() == R.id.like){
                showMessage("Feature will be added later");
            }
            else if(menuItem.getItemId() == R.id.comment){
                showMessage("Feature will be added later");
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
            return true;
        }
    };

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

}
