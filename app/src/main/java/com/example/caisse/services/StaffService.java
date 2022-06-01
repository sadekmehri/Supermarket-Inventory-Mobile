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

import java.util.HashMap;
import java.util.Map;


public class StaffService {

    private final Context context;

    public StaffService(Context context) {
        this.context = context;
    }

    /* Get user details after submitting the jwt token  */
    public void getStaffInfo(AuthRequest authRequest, VolleyResponseListener volleyResponseListener) {
        String url = ConstantsUtils.KEY_URI + "auth/me";

        // Send request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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

                }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Authorization", "Bearer " + authRequest.getToken());
                return params;
            }
        };

        HttpSingleton.getInstance(this.context).addToRequestQueue(jsonObjectRequest);
    }

}
