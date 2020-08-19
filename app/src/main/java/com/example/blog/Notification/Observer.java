package com.example.blog.Notification;

import android.content.Context;

public interface Observer {

    void NotificationAfterNewFollow(String follower, Context context, String topic);

    void IncreaseFollowingFollower(String follower, String following, String following_email, String follower_email);


}