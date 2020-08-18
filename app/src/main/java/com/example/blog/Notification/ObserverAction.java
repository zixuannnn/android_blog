package com.example.blog.Notification;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ObserverAction implements Observer {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref1, ref2;
    @Override
    public void NotificationAfterNewFollow(String message, Context context, String topic) {
        PushNotification push = new PushNotification();
        push.createNotification(context, message, topic);
    }

    @Override
    public void IncreaseFollowingFollower(final String follower, final String following, String following_email, String follower_email) {
        ref1= database.getReference("Users").child(follower);
        ref2 = database.getReference("Users").child(following);

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("followingUsers").child(following).exists()) {
                    int numFollowing = dataSnapshot.child("following").getValue(int.class);
                    numFollowing += 1;
                    database.getReference("Users").child(follower).child("following").setValue(numFollowing);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref1.child("followingUsers").child(following).setValue(following_email);

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child("followerUsers").child(follower).exists()) {
                    int numFollower = dataSnapshot.child("follower").getValue(int.class);
                    numFollower += 1;
                    database.getReference("Users").child(following).child("follower").setValue(numFollower);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref2.child("followerUsers").child(follower).setValue(follower_email);

    }

}
