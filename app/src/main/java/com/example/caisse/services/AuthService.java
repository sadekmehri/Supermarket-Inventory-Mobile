package com.example.caisse.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.caisse.interfaces.VolleyResponseListener;
import com.example.caisse.requests.AuthRequest;
import com.example.caisse.singletons.HttpSingleton;
import com.example.caisse.utils.ConstantsUtils;
import com.example.caisse.utils.MapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AuthService {

    private final Context context;

    public AuthService(Context context) {
        this.context = context;
    }

    /* Login */
    public void login(AuthRequest authRequest, VolleyResponseListener volleyResponseListener) {
        String url = ConstantsUtils.KEY_URI + "auth/login";

        // Add header request
        final Map<String, String> params = setRequestHeader(authRequest);
        JSONObject jsonHeader = new JSONObject(params);

        // Send request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonHeader,
                /* Handle success response */
                response -> {
                    try {
                        Map<String, String> responseMap = MapUtils.convertJsonToMap(response);
                        volleyResponseListener.onResponse(responseMap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },

                /* Handle error response */
                error -> {
                    try {
                        Map<String, String> errMap = MapUtils.onErrorHandler(error);
                        volleyResponseListener.onError(errMap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

        HttpSingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);
    }

    /* Set the request header */
    private Map<String, String> setRequestHeader(AuthRequest authRequest) {
        final Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/json; charset=utf-8");
        params.put("email", authRequest.getEmail());
        params.put("password", authRequest.getPassword());
        return params;
    }

}
