package com.example.blog.Notification;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PushNotification {

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAXq2X69c:APA91bGnrIIjZ15cb4bwvfCBhq9nWe-8NhmswrdJhU-VwfW71N-_9oJqrdJszyR7iRU429NtGVPrePBe0nt0yHdVsJlhnARXoqataqII8jopcnAbh_lvdx3RosEzklbSF0kD_xpFKd3w";
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE = "NEW FOLLOWER";
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    public void createNotification(Context context, String message, String topic){

        TOPIC = "/topics/"+topic; //topic must match with what the receiver subscribed to
        NOTIFICATION_MESSAGE = message;
        JSONObject notification = new JSONObject();
        JSONObject notifcationBody = new JSONObject();
        try {
            notifcationBody.put("title", NOTIFICATION_TITLE);
            notifcationBody.put("message", NOTIFICATION_MESSAGE);
            notification.put("to", TOPIC);
            notification.put("data", notifcationBody);
        } catch (JSONException e) {
            System.out.println("================= created error");
            System.out.println(e.getMessage());
            System.out.println("=================");
        }
        sendNotification(notification, context);

    }

    private void sendNotification(JSONObject notification, Context context) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("=================");
                        System.out.println(response.toString());
                        System.out.println("=================");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("================= send error");
                        System.out.println(error.toString());
                        System.out.println("=================");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        NotificationRequestQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}
