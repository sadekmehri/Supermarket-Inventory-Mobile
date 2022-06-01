package com.example.caisse.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.example.caisse.models.Account;
import com.example.caisse.models.Product;
import com.example.caisse.models.Staff;
import com.example.caisse.models.Store;
import com.example.caisse.singletons.CartSingleton;
import com.example.caisse.singletons.SharedAuthSingleton;
import com.example.caisse.singletons.SharedInvoiceSingleton;
import com.example.caisse.singletons.SharedStaffSingleton;
import com.example.caisse.singletons.ToastSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class MapUtils {

    /* Convert json object to map */
    public static Map<String, String> convertJsonToMap(JSONObject json) throws JSONException {
        Map<String, String> map = new HashMap<>();
        Iterator<String> sIterator = json.keys();
        while (sIterator.hasNext()) {
            String key = sIterator.next();
            String value = json.getString(key);
            map.put(key, value);
        }
        return map;
    }

    @NonNull
    public static Map<String, String> convertStringToMap(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        return convertJsonToMap(jsonObject);
    }

    /* Convert json store data to map */
    public static Store initializeStore(Map<String, String> json) {
        Store store = new Store();
        store.setId(Integer.parseInt(Objects.requireNonNull(json.get("id"))));
        store.setName(json.get("name"));
        store.setPhone(json.get("phone"));
        store.setEmail(json.get("email"));
        store.setAddress(json.get("address"));
        store.setCity(json.get("city"));
        store.setState(json.get("state"));
        store.setZipCode(json.get("zipCode"));

        return store;
    }

    /* Convert json staff data to map */
    public static Staff initializeStaff(Map<String, String> json) throws JSONException {
        Map<String, String> storeJson = convertStringToMap(json.get("store"));
        Store store = initializeStore(storeJson);

        Staff staff = new Staff();
        staff.setId(Integer.parseInt(Objects.requireNonNull(json.get("id"))));
        staff.setFirstName(json.get("firstName"));
        staff.setLastName(json.get("lastName"));
        staff.setPhone(json.get("phone"));
        staff.setDob(json.get("dob"));
        staff.setStore(store);

        return staff;
    }

    /* Convert json account data to map */
    public static Account initializeAccount(Map<String, String> json, String email, String role) throws JSONException {
        Account account = new Account();
        Staff staff = initializeStaff(json);
        account.setStaff(staff);
        account.setRole(role);
        account.setEmail(email);
        return account;
    }

    /* Convert json account data to map */
    public static Product initializeProduct(Map<String, String> json) {
        Product product = new Product();
        product.setId(Integer.parseInt(Objects.requireNonNull(json.get("id"))));
        product.setName(json.get("name"));
        product.setQrCode(json.get("qrCode"));
        product.setQuantity(1);
        product.setPrice(Float.parseFloat(Objects.requireNonNull(json.get("price"))));
        product.calculateTotal();
        return product;
    }

    /* Convert http error response to map */
    public static Map<String, String> onErrorHandler(VolleyError response) throws JSONException {
        NetworkResponse networkResponse = response.networkResponse;
        Map<String, String> errMap = new HashMap<>();

        /* This block is to handle if there is no internet connection */
        if (networkResponse == null) {
            errMap.put("code", "-1");
            return errMap;
        }

        int errCode = networkResponse.statusCode;

        String jsonError = new String(networkResponse.data);
        JSONObject errJson = new JSONObject(jsonError);

        errMap = MapUtils.convertJsonToMap(errJson);
        errMap.put("code", String.valueOf(errCode));

        return errMap;
    }

    public static void handleErrorResponse(Map<String, String> response, Context context) {
        int statusCode = Integer.parseInt(Objects.requireNonNull(response.get("code")));
        HttpStatusUtils httpStatusUtils = HttpStatusUtils.getByCode(statusCode);
        boolean invalidJwtOrRole = statusCode == 401 || statusCode == 403;
        String errMessage = response.get("message");

        if (errMessage == null || errMessage.isEmpty() || invalidJwtOrRole)
            errMessage = httpStatusUtils.getDescription();

        errMessage = StringUtils.capitalize(errMessage);
        ToastSingleton.getInstance(context).toast(errMessage, Toast.LENGTH_LONG);

        /* Logout if the jwt is invalid */
        if (invalidJwtOrRole) {
            CartSingleton.getInstance(context).clear();
            SharedInvoiceSingleton.getInstance(context).clear();
            SharedStaffSingleton.getInstance(context).clear();
            SharedAuthSingleton.getInstance(context).clear();
        }
    }
}
