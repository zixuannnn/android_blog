package com.example.blog.Notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ObserverAction implements FollowObserver, LikeObserver {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref1, ref2;
    private DatabaseReference refPost, refUser;
    @Override
    public void NotificationAfterNewFollow(String message, Context context, String topic) {
        PushNotification push = new PushNotification();
        push.createNotification(context, message, topic);
    }

    @Override
    public void IncreaseFollowingFollower(final String follower, final String following, final String following_email, final String follower_email, final Context context) {
        ref1= database.getReference("Users").child(follower);
        ref2 = database.getReference("Users").child(following);

        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numFollowing = dataSnapshot.child("following").getValue(int.class);
                if(!dataSnapshot.child("followingUsers").child(following).exists()) {
                    numFollowing += 1;
                    database.getReference("Users").child(follower).child("following").setValue(numFollowing);
                    ref1.child("followingUsers").child(following).setValue(following_email);
                    NotificationAfterNewFollow(follower_email+" starts to follow you right now", context, following);
                }
                else{
                    numFollowing -= 1;
                    database.getReference("Users").child(follower).child("following").setValue(numFollowing);
                    ref1.child("followingUsers").child(following).removeValue();
                    showMessage("Successfully unfollow", context);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numFollower = dataSnapshot.child("follower").getValue(int.class);
                if(!dataSnapshot.child("followerUsers").child(follower).exists()) {
                    numFollower += 1;
                    database.getReference("Users").child(following).child("follower").setValue(numFollower);
                    ref2.child("followerUsers").child(follower).setValue(false);
                }
                else {
                    numFollower -= 1;
                    database.getReference("Users").child(following).child("follower").setValue(numFollower);
                    ref2.child("followerUsers").child(follower).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showMessage(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    public void notifyNewLikes(FirebaseUser user, String author, String tag, String title) {
        refPost = database.getReference().child("Posts").child(author).child("likes");
        refUser = database.getReference().child("Users").child(user.getUid());
        if(tag.equals("like")) {
            refPost.child(user.getUid()).setValue(false);
            refUser.child("likes").child(author).setValue(title);
        }
        else{
            refPost.child(user.getUid()).removeValue();
            refUser.child("likes").child(author).removeValue();
        }
    }
}
