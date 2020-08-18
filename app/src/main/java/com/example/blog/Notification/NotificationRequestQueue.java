package com.example.blog.Notification;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class NotificationRequestQueue {

    private  static NotificationRequestQueue queue;
    private RequestQueue requestQueue;
    private Context context;

    private NotificationRequestQueue(Context context) {
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NotificationRequestQueue getInstance(Context context) {
        if (queue == null) {
            queue = new NotificationRequestQueue(context);
        }
        return queue;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    private void showMessage(String s, Context context) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

}
