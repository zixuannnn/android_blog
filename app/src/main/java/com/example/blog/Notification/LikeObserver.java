package com.example.blog.Notification;

import com.google.firebase.auth.FirebaseUser;

public interface LikeObserver {
    void notifyNewLikes(FirebaseUser like, String author, String tag, String title);
}
