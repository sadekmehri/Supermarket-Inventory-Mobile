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

public class CartService {


    private final Context context;

    public CartService(Context context) {
        this.context = context;
    }

    /* Check if the product exists by qrcode */
    public void checkProductAvailability(AuthRequest authRequest, String barCode, VolleyResponseListener volleyResponseListener) {
        String url = ConstantsUtils.KEY_URI + "products/qrcode/" + barCode;

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
