package com.example.blog.Notification;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseIDService extends FirebaseInstanceIdService {

    private static final String SUBSCRIBE_TO = "follower";

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();

        // Once the token is generated, subscribe to topic with the userId
        FirebaseMessaging.getInstance().subscribeToTopic(SUBSCRIBE_TO);
        System.out.println("============ onTokenRefresh completed with token: " + token);
    }
}

