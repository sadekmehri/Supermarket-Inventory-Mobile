package com.example.caisse.interfaces;


import org.json.JSONException;

import java.util.Map;

public interface VolleyResponseListener {
    void onResponse(Map<String, String> response) throws JSONException;

    void onError(Map<String, String> response);
}
