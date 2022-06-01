package com.example.caisse.singletons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

public class ToastSingleton {

    @SuppressLint("StaticFieldLeak")
    private static ToastSingleton instance;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private ToastSingleton(Context context) {
        ctx = context;
    }

    public static synchronized ToastSingleton getInstance(Context context) {
        if (instance == null) instance = new ToastSingleton(context);
        return instance;
    }

    public void toast(String message, int type) {
        Toast.makeText(ctx, message, type).show();
    }

}
