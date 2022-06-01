package com.example.caisse.singletons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.caisse.interfaces.VolleyResponseListener;
import com.example.caisse.models.Account;
import com.example.caisse.requests.AuthRequest;
import com.example.caisse.services.StaffService;
import com.example.caisse.utils.MapUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONException;

import java.util.Map;

public class SharedStaffSingleton {


    private static final String SHARED_PREF_NAME = "volley_staff";
    private static final String KEY_STAFF = "key_staff";

    @SuppressLint("StaticFieldLeak")
    private static SharedStaffSingleton mInstance;

    @SuppressLint("StaticFieldLeak")
    private static Context ctx;

    private SharedStaffSingleton(Context context) {
        ctx = context;
    }

    public static synchronized SharedStaffSingleton getInstance(Context context) {
        if (mInstance == null) mInstance = new SharedStaffSingleton(context);
        return mInstance;
    }

    /* Store the staff data in shared preferences */
    public void userLogin(AuthRequest user) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StaffService staffService = new StaffService(ctx);

        /* Get staff information details process */
        staffService.getStaffInfo(user, new VolleyResponseListener() {
            /* Handle success response */
            @Override
            public void onResponse(Map<String, String> response) throws JSONException {
                Account account = MapUtils.initializeAccount(response,
                        user.getEmail(),
                        user.getRole());

                Gson gson = new Gson();
                String json = gson.toJson(account);

                editor.putString(KEY_STAFF, json);
                editor.apply();
            }

            /* Handle error response */
            @Override
            public void onError(Map<String, String> response) {
                MapUtils.handleErrorResponse(response, ctx);
            }
        });

    }

    /* Give the current logged in user full details */
    public Account getAccount() throws JSONException {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String accountStringObject = sharedPreferences.getString(KEY_STAFF, null);

        /* Convert json to account object */
        JsonParser parser = new JsonParser();
        JsonElement mJson = parser.parse(accountStringObject);
        Gson gson = new Gson();

        return gson.fromJson(mJson, Account.class);
    }

    /* Logout */
    public void clear() {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }


}
