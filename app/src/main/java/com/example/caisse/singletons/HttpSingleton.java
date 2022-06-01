package com.example.caisse.singletons;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpSingleton {

    @SuppressLint("StaticFieldLeak")
    private static HttpSingleton instance;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private RequestQueue requestQueue;

    private HttpSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized HttpSingleton getInstance(Context context) {
        if (instance == null) instance = new HttpSingleton(context);
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
