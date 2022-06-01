package com.example.caisse.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.caisse.interfaces.VolleyResponseListener;
import com.example.caisse.models.Account;
import com.example.caisse.models.Cart;
import com.example.caisse.models.Product;
import com.example.caisse.models.Staff;
import com.example.caisse.requests.AuthRequest;
import com.example.caisse.requests.PaymentRequest;
import com.example.caisse.singletons.HttpSingleton;
import com.example.caisse.singletons.SharedStaffSingleton;
import com.example.caisse.utils.ConstantsUtils;
import com.example.caisse.utils.MapUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentService {

    private final Context context;

    public PaymentService(Context context) {
        this.context = context;
    }

    /* Check if the product exists by qrcode */
    public void paymentProcess(AuthRequest authRequest, Cart cart, VolleyResponseListener volleyResponseListener) throws JSONException {
        final String url = ConstantsUtils.KEY_URI + "payments";

        // Add header request
        Account account = SharedStaffSingleton.getInstance(context).getAccount();
        Staff staff = account.getStaff();
        PaymentRequest paymentRequest = setPaymentsInfo(staff, cart);


        final Map<String, Object> params = setRequestBody(paymentRequest);
        JSONObject jsonBody = new JSONObject(params);

        // Send request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
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

    /* Set request body */
    private Map<String, Object> setRequestBody(PaymentRequest paymentRequest) {
        final Map<String, Object> body = new HashMap<>();

        body.put("productsId", paymentRequest.getProductsIds());
        body.put("productsQty", paymentRequest.getProductsQty());
        body.put("staffId", paymentRequest.getStaffId());
        body.put("storeId", paymentRequest.getStoreId());

        return body;
    }

    /* Set payment request instance */
    private PaymentRequest setPaymentsInfo(Staff staff, Cart cart) {
        PaymentRequest paymentRequest = new PaymentRequest();

        paymentRequest.setProductsIds(getProductsIds(cart));
        paymentRequest.setProductsQty(getProductsQty(cart));
        paymentRequest.setStaffId(staff.getId());
        paymentRequest.setStoreId(staff.getStore().getId());

        return paymentRequest;
    }


    /* Get product ids array */
    private int[] getProductsIds(Cart cart) {
        int cartSize = cart.getSize();
        int[] productIds = new int[cartSize];
        List<Product> lstProduct = cart.getCart();

        for (int i = 0; i < cartSize; i++)
            productIds[i] = lstProduct.get(i).getId();

        return productIds;
    }

    /* Get product qty array */
    private int[] getProductsQty(Cart cart) {
        int cartSize = cart.getSize();
        int[] productQty = new int[cartSize];
        List<Product> lstProduct = cart.getCart();

        for (int i = 0; i < cartSize; i++)
            productQty[i] = lstProduct.get(i).getQuantity();

        return productQty;
    }


}
