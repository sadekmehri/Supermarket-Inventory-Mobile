package com.example.caisse.singletons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.caisse.MainActivity;
import com.example.caisse.requests.AuthRequest;

public class SharedAuthSingleton {

    private static final String SHARED_PREF_NAME = "volley_auth";
    private static final String KEY_EMAIL = "key_email";
    private static final String KEY_ROLE = "key_role";
    private static final String KEY_TOKEN = "key_username";

    @SuppressLint("StaticFieldLeak")
    private static SharedAuthSingleton mInstance;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private SharedAuthSingleton(Context context) {
        ctx = context;
    }

    public static synchronized SharedAuthSingleton getInstance(Context context) {
        if (mInstance == null) mInstance = new SharedAuthSingleton(context);
        return mInstance;
    }

    /* Store the user data in shared preferences */
    public void userLogin(AuthRequest user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_ROLE, user.getRole());
        editor.apply();
    }

    /* Check whether user is already logged in or not */
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL, null) != null;
    }

    /* Give the current logged in user details */
    public AuthRequest getUser() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new AuthRequest(
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_ROLE, null)
        );
    }

    /* Logout */
    public void clear() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        ctx.startActivity(new Intent(ctx, MainActivity.class));
    }

}
